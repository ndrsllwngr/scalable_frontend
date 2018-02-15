package weatherApp.models

case class PartyCreateResponse
(
  id: String,
  name: String,
  password: String,
  createdAt: String
)

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

case class SongListElement
(
  name: String,
  artist: String
)

case class SendSong
(
  streamingServiceID: String,
  name: String,
  artist: String,
  album: String,
  albumCoverUrl: String
)

case class PartyVote
(
  partyID: String,
  songID: Long,
  positive: Boolean,
  voteType: String
)


case class SetSongPlayed
(
  id: Long,
  partyID: String
)

case class AddPhotosToParty
(
  url: String
)

case class PhotoReturn
(
  id: Long,
  url: String,
  upvotes: Int,
  downvote: Int
)


