package weatherApp.pages

import diode.react.ModelProxy
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^.{<, ^, _}
import japgolly.scalajs.react.{BackendScope, Callback, ReactEventFromInput, ScalaComponent}
import org.scalajs.dom.html.Div
import weatherApp.config.Config
import weatherApp.diode.AppState
import weatherApp.router.AppRouter

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

object AdminJoinPage {

  @js.native
  @JSImport("lodash.throttle", JSImport.Default)
  private object _throttle extends js.Any
  def throttle: js.Dynamic = _throttle.asInstanceOf[js.Dynamic]


  case class Props (
                     proxy: ModelProxy[AppState],
                     ctl: RouterCtl[AppRouter.Page]
                   )

  case class AdminJoinState(
                             var code : String,
                             var pw : String
                           )


  class Backend(bs: BackendScope[Props, AdminJoinState]) {
    val host: String = Config.AppConfig.apiHost


    def tryLoginAsAdmin(room: String, pw: String) : Callback = {
      //Callback.alert(s"The Create button was pressed! [$input]")
      if(!room.isEmpty)
        navigateToAdminPage(room)
      else
        Callback.alert("Room Name may not be empty")
    }

    def onTextCodeChange(adminJoinState: AdminJoinState)(e: ReactEventFromInput) = Callback {
      adminJoinState.code = e.target.value
    }

    def onTextPWChange(adminJoinState: AdminJoinState)(e: ReactEventFromInput) = Callback {
      adminJoinState.pw = e.target.value
    }


    def navigateToAdminPage(input: String): Callback = bs.props.flatMap { props =>
      props.ctl.set(AppRouter.AdminRoute(input))
    }

    def render(p: Props, s: AdminJoinState): VdomTagOf[Div] = {
      val proxy = p.proxy()

      <.div(^.cls := "form-group",
        <.label(^.`for` := "roomcode", "Roomcode:"),
        <.div(^.cls := "column", ^.id := "roomcode-row",
          <.div(^.cls := "col-xs-1",
            <.input(^.`type` := "text", ^.cls := "form-control",
              ^.onChange ==> onTextCodeChange(s)
            )
          ),
          <.div(^.cls := "col-xs-2",
            <.label("Password:")
          ),
          <.div(^.cls := "col-xs-3",
            <.input(^.`type` := "text", ^.cls := "form-control",
              ^.onChange ==> onTextPWChange(s)
            )
          ),
          <.div(^.cls := "col-xs-4",
            ^.margin := 8.px,
            <.button(^.`type` := "button", ^.cls := "btn btn-primary custom-button-width",
            ^.onClick --> tryLoginAsAdmin(s.code, s.pw),
            "Login"
          )
        )
        )
      )
    }

  }

  val Component = ScalaComponent.builder[Props]("AdminJoinPage")
    .initialState(AdminJoinState(code = "", pw = ""))
    .renderBackend[Backend]
    .build

}
