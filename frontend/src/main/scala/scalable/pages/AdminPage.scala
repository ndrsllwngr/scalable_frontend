package scalable.pages

import diode.react.ModelProxy
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^.{<, ^, _}
import japgolly.scalajs.react.{BackendScope, ScalaComponent}
import org.scalajs.dom.html.Div

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scala.scalajs.js.undefined
import scalable.components.PlaylistBox
import scalable.config.Config
import scalable.diode.AppState
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
                    roomCode: String,
                    ctl: RouterCtl[AppRouter.Page]
                  )


  class Backend(bs: BackendScope[Props, Unit]) {
    val host: String = Config.AppConfig.apiHost

    def onPlayerReady(e: Event): js.UndefOr[(Event) => Any] = {
      e.target.whenDefined(p => {
        if (p.getVideoUrl().isEmpty){
            nextSong(p)
        }
        else{
          p.playVideo()
        }
        TagMod()
      })
      undefined
    }

    def nextSong(player: Player): Unit = {
      bs.mapProps(props => {
        resolveNext(player, props)
      })
    }


    def resolveNext(player: Player, props: Props): Unit = {
      val state = props.proxy.modelReader.apply()
      val songList = state.songList
      if (songList.isEmpty) {

      } else {
        loadSong(player, songList.head, props.roomCode)
      }
    }

    def loadSong(player: Player, next: Song, roomCode: String): Unit = {
      RestService.setSongPlaying(next.id, roomCode)
      player.loadVideoById(next.streamingServiceID, 0.0, "hd720")
    }

    def onPlayerStateChange(e: Event): js.UndefOr[(Event) => Any] = {
      e.target.whenDefined(p => {
        if (p.getPlayerState() == 1) {
          nextSong(p)
        }
        TagMod()
      })
      undefined
    }

    def onPlayerError(e: Event): js.UndefOr[(Event) => Any] = {
      undefined
    }

    def render(p: Props): VdomTagOf[Div] = {
      val proxy = p.proxy()

      val tag = org.scalajs.dom.document.createElement("script").asInstanceOf[org.scalajs.dom.html.Script]
      tag.src = "https://www.youtube.com/iframe_api"
      val firstScriptTag = org.scalajs.dom.document.getElementsByTagName("script").item(0)
      firstScriptTag.parentNode.insertBefore(tag, firstScriptTag)

      org.scalajs.dom.window.asInstanceOf[js.Dynamic].onYouTubeIframeAPIReady = () => {
        val player = Option.apply(new Player("player", PlayerOptions(
          width = "640",
          height = "360",
          videoId = undefined,
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
      <.div(^.cls := "form-group",
        <.label(^.`for` := "roomcode", s"Room ${p.roomCode}"),
        <.div(^.cls := "column", ^.id := "player-view",
          <.div(^.id := "player"),
          <.div(
            PlaylistBox(PlaylistBox.Props(p.proxy, p.ctl))
          )
        )
      )
    }
  }


  val Component = ScalaComponent.builder[Props]("RoomPage")
    .renderBackend[Backend]
    .build

}
