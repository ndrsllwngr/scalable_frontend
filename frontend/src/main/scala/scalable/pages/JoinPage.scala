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


    def searchForRoomCode(room: String) : Callback = {
      if(!room.isEmpty)
        navigateToHomePage()
      else
        Callback.alert("Room Code may not be empty")
    }

    def onTextChange(roomCodeState: JoinState)(e: ReactEventFromInput) = Callback {
      roomCodeState.input = e.target.value
      AppCircuit.dispatch(SetPartyId(e.target.value))
    }

    def navigateToHomePage(): Callback = bs.props.flatMap {
      props =>{
      RestService.getSongs(props.proxy.value.partyId.get).map{songs =>
        AppCircuit.dispatch(SetSongsForParty(songs))
      }
        props.ctl.set(AppRouter.PlaylistRoute( props.proxy.value.partyId.get))  //TODO

      }
    }

    def render(p: Props, s: JoinState): VdomTagOf[Div] = {
      val proxy = p.proxy()

      <.div(^.cls := "form-group",
        <.label(^.`for` := "roomcode", "Roomcode:"),
        <.div(^.cls := "column", ^.id := "roomcode-row",
          <.div(^.cls := "col-xs-1",
            <.input(^.`type` := "text", ^.cls := "form-control",
              ^.onChange ==> onTextChange(s)
            )
          ),
          <.div(^.cls := "col-xs-2",
            ^.margin := 8.px,
            <.button(^.`type` := "button", ^.cls := "btn btn-primary custom-button-width",
              ^.onClick --> searchForRoomCode(s.input),
              "Search"
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
