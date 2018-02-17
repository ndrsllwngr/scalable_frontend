package scalable.pages

import diode.react.ModelProxy
import japgolly.scalajs.react.{BackendScope, Callback, ScalaComponent}
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom.html.Div

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scalable.config.Config
import scalable.diode.{AppCircuit, AppState, SetPartyId}
import scalable.json.RestService
import scalable.router.AppRouter

object GuestPage {

  @js.native
  @JSImport("lodash.throttle", JSImport.Default)
  private object _throttle extends js.Any
  def throttle: js.Dynamic = _throttle.asInstanceOf[js.Dynamic]


  case class Props (
                     proxy: ModelProxy[AppState],
                     ctl: RouterCtl[AppRouter.Page]
                   )

  case class State(
                  var pageIndex: Int
                  )

  class Backend(bs: BackendScope[Props, State]) {
    val host: String = Config.AppConfig.apiHost

    def navigateToTab(index : Int): Callback = {
        bs.state.map( s => s.pageIndex = index).runNow()
    }


    def render(p: Props, state: State): VdomTagOf[Div] = {
      val proxy = p.proxy()

      <.div(^.cls := "tab",
            <.button(^.`type` := "button", "Playlist" , ^.cls := "btn btn-primary custom-button-width mt-2",
              ^.onClick --> navigateToTab(0)),
              <.button(^.`type` := "button", "FotoFeed" , ^.cls := "btn btn-primary custom-button-width mt-2",
                ^.onClick --> navigateToTab(1))
        
      )
    }

  }

  val Component = ScalaComponent.builder[Props]("AdminJoinPage")
    .initialState(State(0))
    .renderBackend[Backend]
    .build

}
