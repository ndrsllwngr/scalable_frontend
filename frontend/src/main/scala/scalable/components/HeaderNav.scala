package scalable.components

import diode.react.ModelProxy
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._

import scalable.diode.AppState
import scalable.router.AppRouter
import scalable.router.AppRouter.Page

object HeaderNav {
  case class Props(proxy: ModelProxy[AppState], ctl: RouterCtl[Page])

  val NavLink = ScalaFnComponent[TagMod] { el =>
    <.div(el(
      ^.cls := "secondary-hover",
      ^.fontWeight := "bold",
      ^.padding := 10.px
    )
  )
  }

  val Component = ScalaFnComponent[Props](props => {
    val proxy = props.proxy()
    val homeLink = props.ctl.link(AppRouter.HomeRoute)
    val joinLink = props.ctl.link(AppRouter.JoinRoute)
    val joinAsAdminLink = props.ctl.link(AppRouter.JoinAsAdminRoute)
    val createLink = props.ctl.link(AppRouter.CreateRoute)
    val photoFeedLink = props.ctl.link(AppRouter.PhotoRoute("C0LDVK"))

    <.div(
      ^.display := "flex",
      NavLink(homeLink("Home")),
      NavLink(joinLink("Join")),
      NavLink(joinAsAdminLink("Join as Admin")),
      NavLink(createLink("Create")),
      NavLink(photoFeedLink("Gallery")),
    )
  })

  def apply(props: Props) = Component(props)
}
