package weatherApp.models

import java.time.LocalDateTime
import java.util.UUID

import upickle.default.{ReadWriter => RW, macroRW}


case class PartyCreateResponse
(
  id: String,
  name: String,
  password: String,
  createdAt: String
)

object PartyCreateResponse {
  implicit def rw: RW[PartyCreateResponse] = macroRW
}

case class Song
(
  id: Int,
  //id: Long,
  streamingServiceID: String,
  name: String,
  artist: String,
  album: String,
  albumCoverUrl: String,
  upvotes: Int,
  downvotes: Int,
  played: Boolean
  //, createdAt: LocalDateTime
)

object Song {
  implicit def rw: RW[Song] = macroRW
}

case class SongResponse(
                         song: List[Song]
                       )

object SongResponse {
  implicit def rw: RW[SongResponse] = macroRW
}

case class SongListElement
(
  name: String,
  artist: String
)

object SongListElement {
  implicit def rw: RW[SongListElement] = macroRW
}

case class SendSong
(
  streamingServiceID: String,
  name: String,
  artist: String,
  album: String,
  albumCoverUrl: String
)

object SendSong {
  implicit def rw: RW[SendSong] = macroRW
}

case class PartyVote
(
  partyID: String,
  songID: Long,
  positive: Boolean,
  voteType: String
)

object PartyVote {
  implicit def rw: RW[PartyVote] = macroRW
}

case class SetSongPlayed
(
  id: Long,
  partyID: String
)

object SetSongPlayed {
  implicit def rw: RW[SetSongPlayed] = macroRW
}

case class AddPhotosToParty
(
  url: String
)

object AddPhotosToParty {
  implicit def rw: RW[AddPhotosToParty] = macroRW
}

case class PhotoReturn
(
  id: Long,
  url: String,
  upvotes: Int,
  downvote: Int
)

object PhotoReturn {
  implicit def rw: RW[PhotoReturn] = macroRW
}



