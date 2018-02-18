package scalable.pages

import diode.react.ModelProxy
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^.{<, ^, _}
import japgolly.scalajs.react.{BackendScope, Callback, ScalaComponent}
import org.scalajs.dom.html.Div

import scalable.models._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scalable.config.Config
import scalable.diode.{AppCircuit, AppState, SetPartyCreateResponse}
import scalable.services.RestService
import scalable.router.AppRouter

object CreateInfoPage {

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


    def navigateToAdminPage(id: String): Callback = bs.props.flatMap { props =>
      props.ctl.set(AppRouter.AdminRoute(id))
    }

    def render(p: Props): VdomTagOf[Div] = {
      val proxy = p.proxy()
      val dataTxtAttr = VdomAttr("data-text")
      val placeHolderAttr = VdomAttr("placeholder")
      proxy.partyCreateResponse match {
        case None => <.div(
            ^.cls := "d-flex flex-column align-items-center justify-content-center",
            ^.height := "100vh",
            <.div( // LOGO
              ^.cls := "peeledLogo d-flex flex-row align-items-center mb-5 mt-0",
              ^.flex := "0 0 auto",
              <.p(
                ^.aria.label := "CodePen"
                ,<.span(dataTxtAttr := "E","E")
                ,<.span(dataTxtAttr := "R","R")
                ,<.span(dataTxtAttr := "R","R")
                ,<.span(dataTxtAttr := "O","O")
                ,<.span(dataTxtAttr := "R","R")
              )
            ),
            <.div(^.cls := "btn-block mt-2",
              ^.backgroundColor := "#fff",
              ^.maxWidth := 300.px,
              ^.borderRadius := "500px",
              ^.fontWeight := "700",
              ^.textTransform := "uppercase",
              ^.letterSpacing := "3px",
              ^.margin:= "0",
              "Couldn't Create Party. Please go back and try again."
            )
        )
        case partyCreateResponse => <.div(
          ^.cls := "d-flex flex-column align-items-center justify-content-center",
          ^.height := "100vh",
          <.div( // LOGO
            ^.cls := "peeledLogo d-flex flex-row align-items-center mb-5 mt-0",
            ^.flex := "0 0 auto",
            <.p(
              ^.aria.label := "CodePen"
              ,<.span(dataTxtAttr := "H","H")
              ,<.span(dataTxtAttr := "O","O")
              ,<.span(dataTxtAttr := "S","S")
              ,<.span(dataTxtAttr := "T","T")
            )
          ),
          <.div( ^.backgroundColor := "#fff", ^.padding := "10px", ^.borderRadius := "100px",
          <.p (s"Created Party ${partyCreateResponse.get.name}"),
          <.p ("Please save the following Details for Later"),
          <.p (s"Room Code: <b>${partyCreateResponse.get.id}</b>"),
          <.p (s"Password for Room Host Page: <b>${partyCreateResponse.get.password}</b>")),
          <.button(^.`type` := "button", ^.cls := "btn btn-success btn-block mt-2",
          ^.onClick --> navigateToAdminPage(partyCreateResponse.get.id),
          ^.maxWidth := 300.px,
          ^.borderRadius := "500px",
          ^.fontWeight := "700",
          ^.textTransform := "uppercase",
          ^.letterSpacing := "3px",
          ^.margin:= "0",
          "Continue"
        )
        )
      }

    }

  }

  val Component = ScalaComponent.builder[Props]("CreateInfoPage")
    .renderBackend[Backend]
    .build

}
