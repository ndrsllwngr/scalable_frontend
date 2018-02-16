package scalable.diode

import diode.Action

import scalable.models._

case class AppState (
                    partyId:Option[String],
                    songList:List[Song],
                    var currentSong:Option[Song],
                    videoSuggestions: List[VideoResponse],
                    selectedVideo: Option[VideoResponse],
                    isLoading: Boolean
                    )

case class AppModel(
                     state: AppState
                   )



case class SetPartyId(partyId: String) extends Action

case class SetSongsForParty(songList: List[Song]) extends Action

case class GetVideoSuggestions(suggestions: List[VideoResponse]) extends Action

case class SelectVideo(video: Option[VideoResponse]) extends Action

case class SetLoadingState() extends Action

case class ClearLoadingState() extends Action

case class AddSongForParty(partyID: String,
                           song: Song) extends Action

case class VoteSongForParty(partyID: String,
                            songID: Long,
                            positive:Boolean) extends Action

case class CreateParty(partyName: String) extends Action
