package weatherApp.models

import java.time.LocalDateTime
import java.util.UUID

case class Song(id: Long,
                streamingServiceID: String,
                name: String,
                artist: String,
                album: String,
                albumCoverUrl: String,
                createdAt: LocalDateTime)

