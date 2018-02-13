package weatherApp.models


case class VideoResponse(
                     kind: String,
                     etag: String,
                     id: VideoId,
                     snippet: VideoSnippet
                   )


case class VideoId (
                     kind: String,
                     videoId: String,
                     channelId: String,
                     playlistId: String

                   )

case class VideoSnippet (
                          publishedAt: Int,
                          channelId: String,
                          title: String,
                          description: String,
                          channelTitle: String,
                          liveBroadcastContent: String,
                          thumbnails: Map[String, VideoThumbnails]
                   )

case class VideoThumbnails (
                          width: Int,
                          height: Int,
                          url: String
                        )
