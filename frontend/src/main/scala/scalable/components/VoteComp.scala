package scalable.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

import scalable.json._
import scalable.models._


object VoteComp {

  case class Props (voteAble: VoteAble)

  class Backend(bs: BackendScope[Props, Unit]) {

    def vote(voteAble: VoteAble, positive: Boolean) : Callback = Callback {
      RestService.addPartyVote(voteAble.partyID, voteAble.compId, positive, voteAble.voteType)
    }

    def colorGreen(voteAble: VoteAble) : Boolean = {
        if(calcTotal(voteAble) > 0){
          true
        } else {
          false
        }
    }
    def colorRed(voteAble: VoteAble) : Boolean = {
      if(calcTotal(voteAble) < 0){
        true
      } else {
        false
      }
    }
    def calcTotal(voteAble: VoteAble) : Int = {
      voteAble.upvotes - voteAble.downvotes
    }

    def render(props: Props): VdomElement =
      <.div(
        ^.cls := "d-flex flex-column align-items-center align-self-center",
        <.div(
          ^.cls := "align-self-center",
          <.button(
            ^.cls := "btn btn-link",
            ^.onClick --> vote(props.voteAble, positive = true),
            <.img(
              ^.alt := "upvote",
              ^.src := "/images/ic_expand_less_black_24px.svg"
            )
          )),
        <.div(
          ^.classSet(
           "p-2 align-self-center" -> true),
          ^.classSet(
            "text-success" -> colorGreen(props.voteAble),
            "text-danger" -> colorRed(props.voteAble)),
          calcTotal(props.voteAble).toString),
        <.div(
          ^.cls := "align-self-center",
          <.button(
            ^.cls := "btn btn-link",
            ^.onClick --> vote(props.voteAble, positive = false),
            <.img(
              ^.alt := "downvote",
              ^.src := "/images/ic_expand_more_black_24px.svg"
            )
          )
      ))
  }

  val Component = ScalaComponent.builder[Props]("VoteComp")
    .renderBackend[Backend]
    //.configure(Reusability.shouldComponentUpdate)
    .build

  def apply(props: Props) = Component(props)
}
