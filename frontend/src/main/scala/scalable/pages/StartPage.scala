package scalable.pages

import diode.react.ModelProxy
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^.{<, ^, _}
import japgolly.scalajs.react.{BackendScope, Callback, ScalaComponent}
import org.scalajs.dom.html.Div

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scalable.config.Config
import scalable.diode._
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

    def navigateToJoinPage(): Callback = bs.props.flatMap { props =>
      props.ctl.set(AppRouter.JoinRoute)
    }

    def navigateToJoinAsAdminPage(): Callback = bs.props.flatMap { props =>
      props.ctl.set(AppRouter.JoinAsAdminRoute)
    }

    def navigateToCreatePage(): Callback = bs.props.flatMap { props =>
      props.ctl.set(AppRouter.CreateRoute)
    }

    def render(props: Props): VdomTagOf[Div] = {
      val proxy = props.proxy()
      val dataTxtAttr = VdomAttr("data-text")
      <.div(
        ^.cls := "d-flex flex-column align-items-center justify-content-center",
        ^.height := "100vh",
        <.div( // LOGO
          ^.cls := "peeledLogo d-flex flex-row align-items-center mb-5 mt-0",
          ^.flex := "0 0 auto",
          <.p(
            ^.aria.label := "CodePen"
            ,<.span(dataTxtAttr := "S","S")
            ,<.span(dataTxtAttr := "C","C")
            ,<.span(dataTxtAttr := "A","A")
            ,<.span(dataTxtAttr := "L","L")
            ,<.span(dataTxtAttr := "A","A")
            ,<.span(dataTxtAttr := "B","B")
            ,<.span(dataTxtAttr := "L","L")
            ,<.span(dataTxtAttr := "E","E")
          )
        ),
          <.button(^.`type` := "button", ^.cls := "btn btn-primary btn-block",
            ^.backgroundColor := 	"rgba(56, 190, 103,1)",
            ^.borderColor := 	"rgba(56, 190, 103,1)",
            ^.onClick --> navigateToJoinPage(),
            "Join Party"
          ),
        <.div(
          ^.cls := "btn-group-vertical btn-block",
        <.button(^.`type` := "button", ^.cls := "btn btn-outline-primary mt-2 btn-block",
          ^.borderColor := 	"rgba(56, 190, 103,1)",
          ^.color := 	"rgba(56, 190, 103,1)",
          ^.onClick --> navigateToJoinAsAdminPage(),
          "Join Party As Host"
        ),
        <.button(^.`type` := "button", ^.cls := "btn btn-outline-primary btn-block mt-0",
          ^.borderColor := 	"rgba(56, 190, 103,1)",
          ^.color := 	"rgba(56, 190, 103,1)",
          ^.onClick --> navigateToCreatePage(),
          "Create New Party"
        ))
      )
    }
  }

  val Component = ScalaComponent.builder[Props]("StartPage")
    .renderBackend[Backend]
    .build

}
