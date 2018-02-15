package scalable.pages

import diode.react.ModelProxy
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^.{<, ^, _}
import japgolly.scalajs.react.{BackendScope, Callback, ReactEventFromInput, ScalaComponent}
import org.scalajs.dom.html.Div
import scalable.config.Config
import scalable.diode.AppState
import scalable.json.RestService
import scalable.router.AppRouter

import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

object CreatePage {

  @js.native
  @JSImport("lodash.throttle", JSImport.Default)
  private object _throttle extends js.Any

  def throttle: js.Dynamic = _throttle.asInstanceOf[js.Dynamic]


  case class Props(
                    proxy: ModelProxy[AppState],
                    ctl: RouterCtl[AppRouter.Page]
                  )

  case class CreateState(var input: String)


  class Backend(bs: BackendScope[Props, CreateState]) {
    val host: String = Config.AppConfig.apiHost


    def createRoom(input: String) : Callback = {
      val partyCreateResponseFuture = RestService.createParty(input)
      val FutureOfCallback = partyCreateResponseFuture.map(x => navigateToAdminPage(x.id))
      Callback.future(FutureOfCallback)
    }

    def onTextChange(roomCodeState: CreateState)(e: ReactEventFromInput) = Callback {
      roomCodeState.input = e.target.value

    }

    def navigateToAdminPage(id: String): Callback = bs.props.flatMap { props =>
      props.ctl.set(AppRouter.AdminRoute(id))
    }

    def render(p: Props, s: CreateState): VdomTagOf[Div] = {
      val proxy = p.proxy()

      <.div(^.cls := "form-group",
        <.label(^.`for` := "roomcode", "Roomname:"),
        <.div(^.cls := "column", ^.id := "roomcode-row",
          <.div(^.cls := "col-xs-1",
            <.input(^.`type` := "text", ^.cls := "form-control",
              ^.onChange ==> onTextChange(s)
            )
          ),
          <.div(^.cls := "col-xs-2",
            <.label("Password:", ^.`for` := "pw-label")
          ),
          <.div(^.cls := "col-xs-3",
            <.label("Eichelheer", ^.id := "pw-label")
          ),
          <.div(^.cls := "col-xs-4",
            ^.margin := 8.px,
            <.button(^.`type` := "button", ^.cls := "btn btn-primary custom-button-width",
              ^.onClick --> createRoom(s.input),
              "Create"
            )
          )
        )
      )
    }

  }

  val Component = ScalaComponent.builder[Props]("CreatePage")
    .initialState(CreateState(input = ""))
    .renderBackend[Backend]
    .build

}
