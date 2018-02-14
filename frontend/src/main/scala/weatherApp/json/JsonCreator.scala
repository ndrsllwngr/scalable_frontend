package weatherApp.json

import fr.hmil.roshttp.HttpRequest

import scala.concurrent.ExecutionContext.Implicits.global
import weatherApp.config.Config
import weatherApp.models._
import io.circe.syntax._
import io.circe.generic.auto._
import japgolly.scalajs.react.Callback
import org.scalajs.dom
import weatherApp.diode.{AddSongForParty, AppCircuit, CreateParty, VoteSongForParty}
import fr.hmil.roshttp.Protocol.HTTP
import fr.hmil.roshttp.Method._
import fr.hmil.roshttp.body.JSONBody._
import fr.hmil.roshttp.body._
import fr.hmil.roshttp.body.Implicits._
import monix.execution.Scheduler.Implicits.global
import scala.util.{Failure, Success}
import fr.hmil.roshttp.response.SimpleHttpResponse



object JsonCreator {
  val host: String = Config.AppConfig.apiHost

  def createParty(partyName: String): Callback = {
//    val request = HttpRequest()
//      .withProtocol(HTTP)
//      .withPort(5000)
//      .withHost(s"$host")
//      .withPath("/party")
//      .withQueryStringRaw(partyName)
//      .withMethod(PUT)
//
//    request.send().onComplete({
//      case res:Success[SimpleHttpResponse] => println(res.get.body)
//      case e: Failure[SimpleHttpResponse] => println("We got a problem!")
//    })

    Callback {

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
    val content = JSONObjectToJSONBody(PartyVote(partyID, song.id, positive).asJsonObject)
    val request = HttpRequest()
      .withProtocol(HTTP)
      .withPort(5000)
      .withHost(s"$host")
      .withPath("/party/vote")

    request.post(content).onComplete({
      case res:Success[SimpleHttpResponse] => println(res.get.body)
      case e: Failure[SimpleHttpResponse] => println("We got a problem!")
    })

    //val content = PartyVote(partyID, song.id, positive).asJson.asInstanceOf[dom.ext.Ajax.InputData]
    Callback {
      dom.ext.Ajax.post(
      url = s"$host/party/vote",
      data = content,
      headers = Map("Content-Type" -> "application/json")
      ).map(_ => AppCircuit.dispatch(VoteSongForParty(partyID, song.id, positive)))
    }
  }



}
