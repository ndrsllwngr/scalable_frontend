package weatherApp.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra._
import weatherApp.models._

object VoteComp {

  //implicit val reusabilityProps: Reusability[Props] =
  //  Reusability.caseClass

  case class Props (
                          song: Option[Song] = None
                        )

  class Backend(bs: BackendScope[Props, Unit]) {
    def render(props: Props): VdomElement =
      <.div(
        ^.cls := "d-flex flex-column align-items-center align-self-center",
        <.div(
          ^.cls := "align-self-center",
          <.button(
            ^.cls := "btn btn-default",
            <.img(
              ^.alt := "upvote",
              ^.src := "/images/ic_arrow_upward_black_24px.svg"
            )
          )),
        <.div(
          ^.cls := "p-2 align-self-center text-success",
          props.song.map(song => {
            val difference = song.upvotes - song.downvotes
            difference
          }).whenDefined),
        <.div(
          ^.cls := "align-self-center",
          <.button(
            ^.cls := "btn btn-default",
            <.img(
              ^.alt := "downvote",
              ^.src := "/images/ic_arrow_downward_black_24px.svg"
            )
          ))
      )
  }

  val Component = ScalaComponent.builder[Props]("VoteComp")
    .renderBackend[Backend]
    //.configure(Reusability.shouldComponentUpdate)
    .build

  def apply(props: Props) = Component(props)
}
