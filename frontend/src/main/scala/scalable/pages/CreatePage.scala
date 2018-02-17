package scalable.pages

import diode.react.ModelProxy
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^.{<, ^, _}
import japgolly.scalajs.react.{BackendScope, Callback, ReactEventFromInput, ScalaComponent}
import org.scalajs.dom.html.Div

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scalable.config.Config
import scalable.diode.{AppCircuit, AppState, SetPartyCreateResponse, SetPartyId}
import scalable.json.RestService
import scalable.router.AppRouter

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
      partyCreateResponseFuture.map{x =>
        AppCircuit.dispatch(SetPartyCreateResponse(x))
        AppCircuit.dispatch(SetPartyId(x.id))
      }
      navigateToCreateInfoPage()
    }

    def onTextChange(roomCodeState: CreateState)(e: ReactEventFromInput) = Callback {
      roomCodeState.input = e.target.value

    }

    def navigateToCreateInfoPage(): Callback = bs.props.flatMap { props =>
      props.ctl.set(AppRouter.CreateInfoRoute)
    }

    def render(p: Props, s: CreateState): VdomTagOf[Div] = {
      val proxy = p.proxy()

      <.div(^.cls := "d-flex flex-column justify-content-center form-group",
        ^.height := "100vh",
        <.label(^.`for` := "roomcode", "Enter a room name:"),
        <.div(^.cls := "column", ^.id := "roomcode-row",
          <.div(^.cls := "col-xs-1",
            <.input(^.`type` := "text", ^.cls := "form-control",
              ^.onChange ==> onTextChange(s)
            )
          ),
          <.div(
            <.button(^.`type` := "button", ^.cls := "btn btn-primary custom-button-width mt-2",
              ^.onClick --> createRoom(s.input),
              "Create Room"
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
