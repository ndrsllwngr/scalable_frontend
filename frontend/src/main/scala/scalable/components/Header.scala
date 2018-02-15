package scalable.components

import diode.react.ModelProxy
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.{Resolution, RouterCtl}
import japgolly.scalajs.react.vdom.html_<^._

import scalable.diode.AppState
import scalable.router.AppRouter.Page

object Header {
  case class Props(
                    proxy: ModelProxy[AppState],
                    ctl: RouterCtl[Page],
                    resolution: Resolution[Page]
                  )

  val Component = ScalaFnComponent[Props](props => {
    val proxy = props.proxy()
    <.div(
      ^.display := "flex",
      ^.justifyContent := "space-between",
      HeaderNav(HeaderNav.Props(props.proxy, props.ctl))
    )
  })

  def apply(props: Props) = Component(props)
}
