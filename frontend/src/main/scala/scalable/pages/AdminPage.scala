package scalable.pages

import diode.react.ModelProxy
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^.{<, ^, _}
import japgolly.scalajs.react.{BackendScope, Callback, ScalaComponent}
import org.scalajs.dom.html.Div

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scala.scalajs.js.timers.SetIntervalHandle
import scala.scalajs.js.undefined
import scalable.components.{AlreadyPlayedComp, PlaylistBox}
import scalable.config.Config
import scalable.diode.{AppCircuit, AppState, SetSongsForParty}
import scalable.json.RestService
import scalable.models._
import scalable.router.AppRouter

object AdminPage {

  @js.native
  @JSImport("lodash.throttle", JSImport.Default)
  private object _throttle extends js.Any

  def throttle: js.Dynamic = _throttle.asInstanceOf[js.Dynamic]


  case class Props(
                    proxy: ModelProxy[AppState],
                    ctl: RouterCtl[AppRouter.Page]
                  )


  class Backend(bs: BackendScope[Props, Unit]) {
    val host: String = Config.AppConfig.apiHost

    var timer: SetIntervalHandle = _
    var hasSong = false

    var player: Option[Player] = Option.empty
    var props: Props = _


    def onPlayerReady(e: Event): js.UndefOr[(Event) => Any] = {
      e.target.whenDefined(p => {
        if (p.getVideoUrl().isEmpty) {
          resolveNext(p)
        }
        else {
          p.playVideo()
        }
        TagMod()
      })
      undefined
    }

    def resolveNext(player: Player): Unit = {
      println("resolve")
      val state = props.proxy.modelReader.apply()
      var songList = state.songList.filter(s => s.playState.equals("QUEUE"))
      if (songList.isEmpty) {
        hasSong = false
        songList = state.songList
        scala.util.Random.shuffle(songList)
      } else {
        hasSong = true
      }
      if (songList.nonEmpty) props.proxy.value.partyId match {
        case Some(id) => loadSong(player, songList.head, id)
        case None => println("No Party ID")
      }
    }


    def loadSong(player: Player, next: Song, roomCode: String): Unit = {
      println("resolve")
      RestService.setSongPlaying(next.id, roomCode).onComplete(_ => getData())
      player.loadVideoById(next.streamingServiceID, 0.0, "hd720")
    }

    def onPlayerStateChange(e: Event): js.UndefOr[(Event) => Any] = {
      println("state Change")
      e.target.whenDefined(p => {
        println(s"state ${p.getPlayerState()}")
        p.getPlayerState() match {
          case 0 => resolveNext(p)
          case -1 => p.playVideo()
        }

        TagMod()
      })
      undefined
    }

    def onPlayerError(e: Event): js.UndefOr[(Event) => Any] = {
      undefined
    }

    def mounted: Callback = Callback {
      getData()
      startUpdateInterval()
    }

    def unmounted: Callback = Callback {
      println("Unmounted")
      js.timers.clearInterval(timer)
    }

    def startUpdateInterval(): Unit = {
      timer = js.timers.setInterval(10000) { // note the absence of () =>
        getData()
      }
    }

    def getData(): Unit = {

      props.proxy.value.partyId match {
        case Some(id) => RestService.getSongs(id).map { songs =>
          if(songs.nonEmpty && player.isEmpty) {
            createPlayer()
          }

          AppCircuit.dispatch(SetSongsForParty(songs))
        }
        case None => println("NO PARTY ID")
      }
    }

    def createPlayer(): Unit ={
      println("create player")
      val tag = org.scalajs.dom.document.createElement("script").asInstanceOf[org.scalajs.dom.html.Script]
      tag.src = "https://www.youtube.com/iframe_api"
      val firstScriptTag = org.scalajs.dom.document.getElementsByTagName("script").item(0)
      firstScriptTag.parentNode.insertBefore(tag, firstScriptTag)
      org.scalajs.dom.window.asInstanceOf[js.Dynamic].onYouTubeIframeAPIReady = () => {
        player = Some(new Player("player", PlayerOptions(
          width = "640",
          height = "360",
          videoId = "xecWX51PElI",
          events = PlayerEvents(
            onReady = onPlayerReady(_),
            onError = onPlayerError(_),
            onStateChange = onPlayerStateChange(_)
          ),
          playerVars = PlayerVars(
            playsinline = 1.0
          )
        )))
      }
    }


    def logout(props: Props): Callback = {
      props.proxy.value.partyId = Option.empty
      props.ctl.set(AppRouter.StartRoute)
    }

    def render(p: Props): VdomTagOf[Div] = {
      val proxy = p.proxy()
      props = p
      createPlayer()
      var roomCode: String = "NO PARTY ID"
      p.proxy.value.partyId match {
        case Some(id) => roomCode = id
        case None => "NO PARTY ID"

      }


      <.div(^.cls := "form-group",
        <.header(^.cls := "form-group",
          <.button(^.`type` := "button", ^.cls := "btn btn-primary custom-button-width mt-2", ^.onClick --> logout(props), "logout")),
        <.label(^.`for` := "roomcode", s"Room $roomCode"),
        <.div(^.cls := "column", ^.id := "player-view",
          <.div(^.id := "player")
          ,<.div(PlaylistBox(PlaylistBox.Props(p.proxy, p.ctl,  _ => getData(), admin = true)))
          ,<.div(AlreadyPlayedComp(AlreadyPlayedComp.Props(p.proxy, p.ctl)))
        )
      )
    }
  }


  val Component = ScalaComponent.builder[Props]("AdminPage")
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted)
    .componentWillUnmount(scope => scope.backend.unmounted)
    .build

}
