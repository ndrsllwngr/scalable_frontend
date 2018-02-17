package scalable.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

import scala.util.{Failure, Success}
import scalable.json._
import scalable.models._


object VoteComp {

  case class Props (voteAble: VoteAble)

  case class State (var voted: Boolean)

  class Backend(bs: BackendScope[Props, State]) {

    def vote(props: Props, positive: Boolean, state: State) : Callback = Callback {
      if(!state.voted) {
        state.voted = true
        RestService.addPartyVote(props.voteAble.partyID, props.voteAble.compId, positive, props.voteAble.voteType).onComplete{
          case Success(_) => state.voted = true
          case Failure(_) => state.voted = false
        }
      }
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

    def render(props: Props, state: State): VdomElement =
      <.div(
        ^.cls := "d-flex flex-column align-items-center align-self-center",
        <.div(
          ^.cls := "align-self-center",
          <.button(
            ^.cls := "btn btn-link",
            ^.onClick --> vote(props, positive = true, state),
            ^.disabled := state.voted,
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
            ^.onClick --> vote(props, positive = false,state),
            ^.disabled := state.voted,
            <.img(
              ^.alt := "downvote",
              ^.src := "/images/ic_expand_more_black_24px.svg"
            )
          )
      ))
  }

  val Component = ScalaComponent.builder[Props]("VoteComp")
    .initialState(State(false))
    .renderBackend[Backend]
    .build

  def apply(props: Props) = Component(props)
}
