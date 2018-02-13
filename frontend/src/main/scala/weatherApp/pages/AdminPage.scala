package weatherApp.pages

import diode.react.ModelProxy
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^.{<, ^, _}
import japgolly.scalajs.react.{BackendScope, ScalaComponent}
import org.scalajs.dom.html.Div
import weatherApp.config.Config
import weatherApp.diode.AppState
import weatherApp.models._
import weatherApp.router.AppRouter

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scala.scalajs.js.undefined

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


    def render(p: Props): VdomTagOf[Div] = {
      val proxy = p.proxy()

      val tag = org.scalajs.dom.document.createElement("script").asInstanceOf[org.scalajs.dom.html.Script]
      tag.src = "https://www.youtube.com/iframe_api"
      val firstScriptTag = org.scalajs.dom.document.getElementsByTagName("script").item(0)
      firstScriptTag.parentNode.insertBefore(tag, firstScriptTag)
      org.scalajs.dom.window.asInstanceOf[js.Dynamic].onYouTubeIframeAPIReady = () => {
        val player : Player= new Player("player", PlayerOptions(
          width = "640",
          height = "360",
          videoId = "M7lc1UVf-VE",
          events = PlayerEvents(
            onReady = onPlayerReady (_),
            onError = onPlayerError (_),
            onStateChange = onPlayerStateChange (_)
          ),
          playerVars = PlayerVars(
            playsinline = 1.0
          )
        ))
      }

      <.div(^.cls := "form-group",
        <.label(^.`for` := "roomcode", s"Roomcode ${p.roomCode}"),
        <.div(^.cls := "column", ^.id := "player-view",
          <.div(^.id := "player")
          // TODO put playlist here
        )
      //  <.iframe(^.id := "player", ^.`type` := "text/html", ^.width := "640", ^.height := "360",
       //   ^.src := "http://www.youtube.com/embed/M7lc1UVf-VE?enablejsapi=1&origin=http://example.com")
      )
    }
  }

  def onPlayerReady(e: Event): js.UndefOr[(Event) => Any] = {
    e.target.whenDefined(p => {
      p.playVideo()
      TagMod()
    })
    undefined
  }

  def onPlayerStateChange(e: Event): js.UndefOr[(Event) => Any] = {
    undefined
  }

  def onPlayerError(e: Event): js.UndefOr[(Event) => Any] = {
    undefined
  }

  val Component = ScalaComponent.builder[Props]("RoomPage")
    .renderBackend[Backend]
    .build

}
