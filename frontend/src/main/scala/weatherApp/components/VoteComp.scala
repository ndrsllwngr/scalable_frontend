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
    def colorGreen(song: Song) : Boolean = {
        if(calcTotal(song) > 0){
          true
        } else {
          false
        }
    }
    def colorRed(song: Song) : Boolean = {
      if(calcTotal(song) < 0){
        true
      } else {
        false
      }
    }
    def calcTotal(song: Song) : Int = {
      song.upvotes - song.downvotes
    }
    def render(props: Props): VdomElement =
      <.div(
        ^.cls := "d-flex flex-column align-items-center align-self-center",
        <.div(
          ^.cls := "align-self-center",
          <.button(
            ^.cls := "btn btn-default",
            <.img(
              ^.alt := "upvote",
              ^.src := "/images/ic_expand_less_black_24px.svg"
            )
          )),
        <.div(
          ^.classSet(
           "p-2 align-self-center" -> true),
          props.song.map(song => {
            ^.classSet(
              "text-success" -> colorGreen(song),
              "text-danger" -> colorRed(song))
          }).whenDefined,
          props.song.map(song => {
            calcTotal(song).toString
          }).whenDefined),
        <.div(
          ^.cls := "align-self-center",
          <.button(
            ^.cls := "btn btn-default",
            <.img(
              ^.alt := "downvote",
              ^.src := "/images/ic_expand_more_black_24px.svg"
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
