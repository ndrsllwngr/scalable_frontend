package weatherApp.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra._

object VoteComp {

  //implicit val reusabilityProps: Reusability[Props] =
  //  Reusability.caseClass

  final case class State(id: Int)

  final class Backend($: BackendScope[Unit, State]) {
    def render(s: State): VdomElement =
        <.div(
            ^.cls := "d-flex flex-column align-items-center align-self-center",
          <.div(
            ^.cls := "align-self-center",
            <.button(
              ^.cls := "btn btn-default",
              <.img(
                ^.alt := "upvote",
                ^.src := "./images/ic_arrow_upward_black_24px.svg"
              )
            )),
          <.div(
            ^.cls := "p-2 align-self-center text-success",
            "+3"),
          <.div(
            ^.cls := "align-self-center",
            <.button(
              ^.cls := "btn btn-default",
              <.img(
                ^.alt := "downvote",
                ^.src := "./images/ic_arrow_downward_black_24px.svg"
              )
            ))
      )
  }

  val Component = ScalaComponent.builder[Unit]("VoteComp")
      .initialState(State(0))
    .renderBackend[Backend]
    //.configure(Reusability.shouldComponentUpdate)
    .build

  def apply() = Component()
}
