package weatherApp.pages

import diode.react.ModelProxy
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^.{<, ^, _}
import japgolly.scalajs.react.{BackendScope, ScalaComponent}
import org.scalajs.dom.html.Div
import weatherApp.diode.AppState
import weatherApp.router.AppRouter

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

object RoomPage {

  @js.native
  @JSImport("lodash.throttle", JSImport.Default)
  private object _throttle extends js.Any
  def throttle: js.Dynamic = _throttle.asInstanceOf[js.Dynamic]


  case class Props (
                     proxy: ModelProxy[AppState],
                     ctl: RouterCtl[AppRouter.Page]
                   )


  class Backend($: BackendScope[Props,Unit]) {


    def render(p: Props): VdomTagOf[Div] = {
      val proxy = p.proxy()

      <.div(^.cls := "form-group",
        <.label(^.`for` := "roomcode", "Roomcode:"),
        <.div(^.cls := "row", ^.id := "roomcode",
          <.div(^.cls := "col-xs-10",
            <.input(^.`type` := "text", ^.cls := "form-control")
          ),
          <.div(^.cls := "col-xs-2",
            <.button(^.`type` := "button", ^.cls := "btn btn-primary custom-button-width",
              "Search"
            )
          )
        )
      )
    }
      
  }

  val Component = ScalaComponent.builder[Props]("RoomPage")
    .renderBackend[Backend]
    .build

}
