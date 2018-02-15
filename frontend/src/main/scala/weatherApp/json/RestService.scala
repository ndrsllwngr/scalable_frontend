package weatherApp.json

import scala.concurrent.ExecutionContext.Implicits.global
import weatherApp.config.Config
import weatherApp.models._
import org.scalajs.dom.ext.Ajax

import scala.concurrent.Future
import io.circe.parser.decode
import io.circe.generic.auto._
import io.circe.syntax._
import slogging.StrictLogging


object RestService extends StrictLogging{

  val host: String = Config.AppConfig.apiHost

  def createParty(partyName: String): Future[PartyCreateResponse] = {
    val content = partyName.asInstanceOf[Ajax.InputData]
    Ajax.put(
      url = s"$host/party",
      data = content,
      headers = Map("Content-Type" -> "text/plain")
    ).map { res =>
      val option = decode[PartyCreateResponse](res.responseText)
      option match {
        case Left(_) => PartyCreateResponse("", "", "", "")
        case Right(partyCreateResponse) => partyCreateResponse
      }
    }
  }

  def addSongToParty(partyID: String, sendSong: SendSong): Future[Song] = {
    val content = sendSong.asJson.asInstanceOf[Ajax.InputData]
    Ajax.put(
      url = s"$host/party/song/$partyID",
      data = content,
      headers = Map("Content-Type" -> "application/json")
    ).map { res =>
      val option = decode[Song](res.responseText)
      option match {
        case Left(_) => Song(0, "", "", "", "", "", 0, 0, false)
        case Right(song) => song
      }
    }
  }

  def getSongs(partyID: String): Future[List[Song]] = {
    Ajax.get(
      url = s"$host/party/song/$partyID"
    ).map { res =>
      val option = decode[List[Song]](res.responseText)
      option match {
        case Left(_) => List.empty[Song]
        case Right(songList) => songList
      }
    }
  }

  def addPartyVote(partyID: String, song: Song, positive: Boolean, voteType: String): Future[Int] = {
    val content = PartyVote(partyID, song.id, positive, voteType).asJson.asInstanceOf[Ajax.InputData]
    Ajax.post(
      url = s"$host/vote",
      data = content,
      headers = Map("Content-Type" -> "application/json")
    ).map { res =>
      val option = decode[Int](res.responseText)
      option match {
        case Left(_) => -1
        case Right(int) => int
      }
    }
  }

  def setSongPlayed(songID: Long, partyID: String): Future[Int] = {
    val content = SetSongPlayed(songID, partyID).asJson.asInstanceOf[Ajax.InputData]
    Ajax.post(
      url = s"$host/party/song",
      data = content,
      headers = Map("Content-Type" -> "application/json")
    ).map { res =>
      val option = decode[Int](res.responseText)
      option match {
        case Left(_) => -1
        case Right(int) => int
      }
    }
  }

  def addPhoto(downloadUrl: String, partyID: String): Future[Int] ={
    val content = AddPhotosToParty(downloadUrl).asJson.asInstanceOf[Ajax.InputData]
    logger.debug(content.toString)
    Ajax.put(
      url = s"$host/party/photo/$partyID",
      data = content,
      headers = Map("Content-Type" -> "application/json")
    ).map { res =>
      val option = decode[Int](res.responseText)
      option match {
        case Left(_) => -1
        case Right(int) => int
      }
    }
  }

  def getPhotos(partyID: String): Future[List[PhotoReturn]] = {
    Ajax.get(
      url = s"$host/party/photo/$partyID"
    ).map { res =>
      val option = decode[List[PhotoReturn]](res.responseText)
      option match {
        case Left(_) => List.empty[PhotoReturn]
        case Right(songList) => songList
      }
    }
  }

}
