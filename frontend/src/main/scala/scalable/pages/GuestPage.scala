package scalable.pages

import diode.react.ModelProxy
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{BackendScope, Callback, ScalaComponent}
import org.scalajs.dom.html.Div

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scalable.components.{PhotoFeedTab, PlaylistTab}
import scalable.config.Config
import scalable.diode.AppState
import scalable.router.AppRouter

object GuestPage {

  @js.native
  @JSImport("lodash.throttle", JSImport.Default)
  private object _throttle extends js.Any

  def throttle: js.Dynamic = _throttle.asInstanceOf[js.Dynamic]


  case class Props( proxy: ModelProxy[AppState], ctl: RouterCtl[AppRouter.Page])

  case class State( var pageIndex: Int )

  class Backend(bs: BackendScope[Props, State]) {
    val host: String = Config.AppConfig.apiHost

    def navigateToTab(index: Int): Callback = {
      bs.modState(s =>{
        s.pageIndex = index
        s
      }).runNow()
      Callback.empty
    }

    def logout(props: Props): Callback ={
      props.proxy.value.partyId = Option.empty
      props.ctl.set(AppRouter.StartRoute)
    }


    def render(p: Props, state: State): VdomTagOf[Div] = {
      var tabContent : VdomTagOf[Div] = <.div(PlaylistTab(PlaylistTab.Props(p.proxy, p.ctl, admin = false)))
      state.pageIndex match{
        case 0 => tabContent = <.div(PlaylistTab(PlaylistTab.Props(p.proxy, p.ctl, admin = false)))
        case 1 => tabContent = <.div(PhotoFeedTab(PhotoFeedTab.Props(p.proxy, p.ctl, admin = false)))
      }

      <.div(
        <.header(^.cls := "form-group",
          <.button(^.`type` := "button", ^.cls := "btn btn-primary custom-button-width mt-2", ^.onClick --> logout(p), "logout")),

        <.div(^.cls := "tab",
        <.button(^.`type` := "button", "Playlist", ^.onClick --> navigateToTab(0)),
        <.button(^.`type` := "button", "Fotofeed", ^.onClick --> navigateToTab(1))
      ), tabContent
      )
    }

  }

  val Component = ScalaComponent.builder[Props]("GuestPage")
    .initialState(State(0))
    .renderBackend[Backend]
    .build

}
