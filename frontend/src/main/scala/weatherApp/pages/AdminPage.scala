package weatherApp.pages

import diode.react.ModelProxy
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^.{<, ^, _}
import japgolly.scalajs.react.{BackendScope, ScalaComponent}
import org.scalajs.dom.html.Div
import weatherApp.config.Config
import weatherApp.diode.AppState
import weatherApp.router.AppRouter

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

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

      <.div(^.cls := "form-group",
        <.label(^.`for` := "roomcode", "Roomcode:"),
        <.div(^.cls := "row", ^.id := "roomcode-row",
          <.div(^.cls := "col-xs-10",
            <.input(^.`type` := "text", ^.cls := "form-control",
              ^.value := p.roomCode
            )
          )
        ),
        <.iframe(^.id := "player", ^.`type` := "text/html", ^.width := "640", ^.height := "360",
          ^.src := "http://www.youtube.com/embed/M7lc1UVf-VE?enablejsapi=1&origin=http://example.com")
      )
    }
  }

  val Component = ScalaComponent.builder[Props]("RoomPage")
    .renderBackend[Backend]
    .build

}
