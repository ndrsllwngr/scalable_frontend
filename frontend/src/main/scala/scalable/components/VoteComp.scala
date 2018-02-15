package scalable.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import scalable.json._
import scalable.models._


object VoteComp {

  case class Props (
                          partyID: String,
                          song: Song
                        )

  class Backend(bs: BackendScope[Props, Unit]) {

    def vote(partyID: String, song: Song, positive: Boolean) : Callback = Callback {
      RestService.addPartyVote(partyID, song, positive, "SONG")
    }
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
            ^.cls := "btn btn-link",
            ^.onClick --> vote(props.partyID, props.song, positive = true),
            <.img(
              ^.alt := "upvote",
              ^.src := "/images/ic_expand_less_black_24px.svg"
            )
          )),
        <.div(
          ^.classSet(
           "p-2 align-self-center" -> true),
          ^.classSet(
            "text-success" -> colorGreen(props.song),
            "text-danger" -> colorRed(props.song)),
          calcTotal(props.song).toString,
        <.div(
          ^.cls := "align-self-center",
          <.button(
            ^.cls := "btn btn-link",
            ^.onClick --> vote(props.partyID, props.song, positive = false),
            <.img(
              ^.alt := "downvote",
              ^.src := "/images/ic_expand_more_black_24px.svg"
            )
          ))
      ))
  }

  val Component = ScalaComponent.builder[Props]("VoteComp")
    .renderBackend[Backend]
    //.configure(Reusability.shouldComponentUpdate)
    .build

  def apply(props: Props) = Component(props)
}
