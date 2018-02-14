package weatherApp.json

import scala.concurrent.ExecutionContext.Implicits.global
import weatherApp.config.Config
import weatherApp.models._
import io.circe.syntax._
import io.circe.generic.auto._
import japgolly.scalajs.react.Callback
import org.scalajs.dom
import weatherApp.diode.{AddSongForParty, AppCircuit, CreateParty, VoteSongForParty}


object JsonCreator {
  val host: String = Config.AppConfig.apiHost

  def createParty(partyName: String): Callback = {
    Callback {
      dom.ext.Ajax.put(
        url = s"$host/party",
        data = partyName
      ).map(_ => AppCircuit.dispatch(CreateParty(partyName)))
    }
  }

  def addSongToParty(partyID: String, song: Song): Callback = {
    val content = song.asJson.asInstanceOf[dom.ext.Ajax.InputData]
    Callback {
      dom.ext.Ajax.post(
        url = s"$host/party/song/$partyID",
        data = content,
        headers = Map("Content-Type" -> "application/json")
      ).map(_ => AppCircuit.dispatch(AddSongForParty(partyID, song)))
    }
  }


  def addPartyVote(partyID: String, song: Song, positive: Boolean): Callback = {
    val content = PartyVote(partyID, song.id, positive).asJson.asInstanceOf[dom.ext.Ajax.InputData]
    Callback {
      dom.ext.Ajax.post(
      url = s"$host/party/vote",
      data = content,
      headers = Map("Content-Type" -> "application/json")
      ).map(_ => AppCircuit.dispatch(VoteSongForParty(partyID, song.id, positive)))
    }
  }



}
