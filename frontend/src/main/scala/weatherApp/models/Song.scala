package weatherApp.models

import java.time.LocalDateTime
import java.util.UUID

case class Song(
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

case class SongResponse(
                         id: Int,
                         song: List[Song]
                       )

case class SongListElement(
                            name: String,
                            artist: String
                          )

