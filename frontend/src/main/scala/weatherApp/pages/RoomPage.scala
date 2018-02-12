package weatherApp.pages

import diode.react.ModelProxy
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^.{<, ^, _}
import japgolly.scalajs.react.{BackendScope, Callback, ReactEventFromInput, ScalaComponent}
import org.scalajs.dom.html.Div
import weatherApp.config.Config
import weatherApp.diode.AppState
import weatherApp.router.AppRouter

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

object RoomPage {

  @js.native
  @JSImport("lodash.throttle", JSImport.Default)
  private object _throttle extends js.Any
  def throttle: js.Dynamic = _throttle.asInstanceOf[js.Dynamic]


  case class Props (
                     proxy: ModelProxy[AppState],
                     ctl: RouterCtl[AppRouter.Page]
                   )

  case class RoomCodeState( var input : String)


  class Backend(bs: BackendScope[Props, RoomCodeState]) {
    val host: String = Config.AppConfig.apiHost


    def searchForRoomCode(room: String) : Callback = {
      if(!room.isEmpty)
        navigateToHomePage()
      else
        Callback.alert("Room Code may not be empty")
    }


    def createRoom(input: String) : Callback = {
      //Callback.alert(s"The Create button was pressed! [$input]")
      if(!input.isEmpty)
        navigateToAdminPage(input)
      else
        Callback.alert("Room Code may not be empty")
    }

    def onTextChange(roomCodeState: RoomCodeState)(e: ReactEventFromInput) = Callback {
      roomCodeState.input = e.target.value

    }

    def navigateToHomePage(): Callback = bs.props.flatMap { props =>
      props.ctl.set(AppRouter.HomeRoute)
    }

    def navigateToAdminPage(input: String): Callback = bs.props.flatMap { props =>
      props.ctl.set(AppRouter.AdminRoute(input))
    }

    def render(p: Props, s: RoomCodeState): VdomTagOf[Div] = {
      val proxy = p.proxy()

      <.div(^.cls := "form-group",
        <.label(^.`for` := "roomcode", "Roomcode:"),
        <.div(^.cls := "row", ^.id := "roomcode-row",
          <.div(^.cls := "col-xs-10",
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
          ),
          <.div(^.cls := "col-xs-3",
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

  val Component = ScalaComponent.builder[Props]("RoomPage")
    .initialState(RoomCodeState(input = ""))
    .renderBackend[Backend]
    .build

}
