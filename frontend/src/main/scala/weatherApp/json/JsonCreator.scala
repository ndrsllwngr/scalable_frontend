package weatherApp.json

import scala.concurrent.ExecutionContext.Implicits.global
import weatherApp.config.Config
import weatherApp.models._
import io.circe.syntax._
import io.circe.generic.auto._
import japgolly.scalajs.react.Callback
import org.scalajs.dom
import weatherApp.diode.{AppCircuit, VoteSongForParty}


object JsonCreator {
  val host: String = Config.AppConfig.apiHost

  def addPartyVote(partyID: String, song: Song, positive: Boolean): Callback = {
    val partyVote = PartyVote(partyID, song.id, positive).asJson.asInstanceOf[dom.ext.Ajax.InputData]
    Callback {
      dom.ext.Ajax.post(
        url = s"$host/party/vote",
        data = partyVote,
        headers = Map("Content-Type" -> "application/json")
      ).map(_ => AppCircuit.dispatch(VoteSongForParty(partyID, song.id, positive)))
    }
  }
}
