package scalable.pages

import diode.react.ModelProxy
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{BackendScope, Callback, ScalaComponent}
import org.scalajs.dom.html.Div

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scalable.components.{AdminTab, PhotoFeedTab}
import scalable.config.Config
import scalable.diode.AppState
import scalable.router.AppRouter

object AdminPage {

  @js.native
  @JSImport("lodash.throttle", JSImport.Default)
  private object _throttle extends js.Any

  def throttle: js.Dynamic = _throttle.asInstanceOf[js.Dynamic]

  case class Props( proxy: ModelProxy[AppState], ctl: RouterCtl[AppRouter.Page])


  class Backend(bs: BackendScope[Props, Unit]) {
    val host: String = Config.AppConfig.apiHost


    def logout(props: Props): Callback ={
      props.proxy.value.partyId = Option.empty
      props.ctl.set(AppRouter.StartRoute)
    }


    def render(p: Props): VdomTagOf[Div] = {

      <.div(
        <.header(^.cls := "form-group",
          <.button(^.`type` := "button", ^.cls := "btn btn-primary custom-button-width mt-2", ^.onClick --> logout(p), "logout")),

        <.div(^.cls := "d-flex", ^.marginRight := 10.px,
          <.div(AdminTab(AdminTab.Props(p.proxy, p.ctl))),
          <.div(PhotoFeedTab(PhotoFeedTab.Props(p.proxy, p.ctl, admin = true)))
        )
      )
    }

  }

  val Component = ScalaComponent.builder[Props]("AdminPage")
    .renderBackend[Backend]
    .build

}
