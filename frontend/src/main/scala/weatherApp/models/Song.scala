package weatherApp.models

import java.time.LocalDateTime
import java.util.UUID

case class Song(
                 id: Int,
                 streamingServiceID: String,
                 name: String,
                 artist: String,
                 album: String,
                 albumCoverUrl: String
                 //, createdAt: LocalDateTime
               )

case class SongResponse(
                         id: Int,
                         song: List[Song]
                       )

case class SongListElement(
                            name: String,
                            artist: String
                          )

case class Votes(
                  id: Long,
                  count: Int
                )

