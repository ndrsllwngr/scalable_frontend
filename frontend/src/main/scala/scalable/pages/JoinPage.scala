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
import scalable.diode._
import scalable.json.RestService
import scalable.router.AppRouter

object JoinPage {

  @js.native
  @JSImport("lodash.throttle", JSImport.Default)
  private object _throttle extends js.Any
  def throttle: js.Dynamic = _throttle.asInstanceOf[js.Dynamic]


  case class Props (
                     proxy: ModelProxy[AppState],
                     ctl: RouterCtl[AppRouter.Page]
                   )

  case class JoinState( var input : String)


  class Backend(bs: BackendScope[Props, JoinState]) {
    val host: String = Config.AppConfig.apiHost


    def join (partyID: String) : Callback = {
      if(!partyID.isEmpty) {
        val partyExistsBooleanFuture = RestService.joinParty(partyID)
        val futureCallback = partyExistsBooleanFuture.map { exists =>
          if (exists) {
            AppCircuit.dispatch(SetPartyId(partyID))
            navigateToHomePage()
          } else
            Callback.alert("PartyCode does not exist")
        }
        Callback.future(futureCallback)
      } else
      Callback.alert("Room Code may not be empty")
    }

    def onTextChange(roomCodeState: JoinState)(e: ReactEventFromInput) = Callback {
      roomCodeState.input = e.target.value
    }

    def navigateToHomePage(): Callback = bs.props.flatMap {
      props =>{
      RestService.getSongs(props.proxy.value.partyId.get).map{songs =>
        AppCircuit.dispatch(SetSongsForParty(songs))
      }
        props.ctl.set(AppRouter.GuestRoute( props.proxy.value.partyId.get))
      }
    }

    def render(p: Props, s: JoinState): VdomTagOf[Div] = {
      val proxy = p.proxy()
      val dataTxtAttr = VdomAttr("data-text")
      <.div(^.cls := "d-flex flex-column justify-content-center form-group",
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
        <.label(^.`for` := "roomcode", "Roomcode:"),
        <.div(^.cls := "column", ^.id := "roomcode-row",
          <.div(^.cls := "col-xs-1",
            <.input(^.`type` := "text", ^.cls := "form-control",
              ^.onChange ==> onTextChange(s)
            )
          ),
          <.div(
            <.button(^.`type` := "button", ^.cls := "btn btn-primary custom-button-width mt-2",
              ^.backgroundColor := 	"rgba(56, 190, 103,1)",
              ^.borderColor := 	"rgba(56, 190, 103,1)",
              ^.onClick --> join(s.input),
              "Join"
            )
          )
        )
      )
    }

  }

  val Component = ScalaComponent.builder[Props]("JoinPage")
    .initialState(JoinState(input = ""))
    .renderBackend[Backend]
    .build

}
