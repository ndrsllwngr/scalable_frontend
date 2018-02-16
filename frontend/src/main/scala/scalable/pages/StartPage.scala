package scalable.pages

import diode.react.ModelProxy
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^.{<, ^, _}
import japgolly.scalajs.react.{BackendScope, Callback, ReactEventFromInput, ScalaComponent, ScalaFnComponent}
import org.scalajs.dom.html.Div

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scalable.components.HeaderNav.NavLink
import scalable.config.Config
import scalable.diode._
import scalable.json.RestService
import scalable.router.AppRouter

object StartPage {

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



    val NavLink = ScalaFnComponent[TagMod] { el =>
      <.div(el(
        ^.cls := "secondary-hover",
        ^.fontWeight := "bold",
        ^.padding := 10.px
      )
      )
    }
    def render(props: Props): VdomTagOf[Div] = {

      val proxy = props.proxy()
      val joinLink = props.ctl.link(AppRouter.JoinRoute)
      val joinAsAdminLink = props.ctl.link(AppRouter.JoinAsAdminRoute)
      val createLink = props.ctl.link(AppRouter.CreateRoute)
      val photoFeedLink = props.ctl.link(AppRouter.PhotoRoute("C0LDVK"))

      <.div(
        ^.display := "flex",
        NavLink(joinLink("Join Party")),
        NavLink(joinAsAdminLink("Join Party As Host")),
        NavLink(createLink("Create New Party"))
      )
    }
  }

  val Component = ScalaComponent.builder[Props]("StartPage")
    .renderBackend[Backend]
    .build

}
