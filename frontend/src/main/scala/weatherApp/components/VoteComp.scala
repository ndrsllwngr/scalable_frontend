package weatherApp.components

import scala.concurrent.ExecutionContext.Implicits.global
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra._
import org.scalajs.dom
import weatherApp.config.Config
import weatherApp.models._
import weatherApp.json._
import weatherApp.diode.{AppCircuit, RemoveCityFromFavs, VoteSongForParty}


object VoteComp {

  //implicit val reusabilityProps: Reusability[Props] =
  //  Reusability.caseClass

  case class Props (
                          partyID: String = "partyID",
                          song: Option[Song] = None
                        )

  class Backend(bs: BackendScope[Props, Unit]) {
//    val host: String = Config.AppConfig.apiHost

//    def addPartyVote(partyID: String, song: Song, positive: Boolean) : Callback = {
//        val partyVote = PartyVote(partyID, song.id, positive).asJson.asInstanceOf[dom.ext.Ajax.InputData]
//        Callback {
//          dom.ext.Ajax.post(
//            url = s"$host/party/vote",
//            data = partyVote,
//            headers = Map("Content-Type" -> "application/json")
//          ).map(_ => AppCircuit.dispatch(VoteSongForParty(partyID, song.id, positive)))
//        }
//    }

    def vote(partyID: String, song: Song, positive: Boolean) : Callback = Callback {
      restService.addPartyVote(partyID, song, positive, "SONG")
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
            ^.onClick --> vote(props.partyID, props.song.head, positive = true),
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
            ^.cls := "btn btn-link",
            ^.onClick --> vote(props.partyID, props.song.head, positive = false),
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
