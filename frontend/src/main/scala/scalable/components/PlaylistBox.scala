package scalable.components

import diode.Action
import diode.react.ModelProxy

import scala.concurrent.ExecutionContext.Implicits.global
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra.router.RouterCtl
import org.scalajs.dom
import scalable.config.Config
import scalable.models._
import scalable.router.AppRouter
import io.circe.syntax._
import io.circe.generic.auto._
import scalable.diode._

object PlaylistBox {

  case class Props (
                     proxy: ModelProxy[AppState],
                     ctl: RouterCtl[AppRouter.Page]
                   )

  class Backend(bs: BackendScope[Props, Unit]) {
    def mounted: Callback = Callback.log("Mounted!")

    val host: String = Config.AppConfig.apiHost

    def render(props: Props): VdomElement = {
      <.div(getSongs(props).toVdomArray)
    }
  }

  def getSongs(props: Props) ={
    val proxy                        = props.proxy()
    //val dispatch: Action => Callback = props.proxy.dispatchCB
    val songs                        = proxy.songList
    val partyId                      = proxy.partyId
    partyId match {
      case Some(id) => songs.map(x => {
        songView(x,id)
      })
      case None => Seq(<.p("No party ID set"))
    }

  }

  def songView(song:Song, partyID:String ) ={
    val id = song.id
    val name = song.name
    val artist = song.artist
    val albumCoverUrl = song.albumCoverUrl
    <.div( // Playlist Row
      ^.cls := "d-flex flex-row justify-content-start bg-white text-dark p-2",
      ^.maxWidth := "800px",
      ^.borderWidth := "2px 0 0 0",
      ^.borderStyle := "solid",
      ^.borderColor := "black",
      <.div(
        ^.cls := "d-flex flex-row justify-content-start mr-auto",
        <.div(
          ^.cls := "", // AlbumCover
          <.img(
            ^.cls := "",
            ^.maxWidth := 118.px,
            ^.src := albumCoverUrl
          )
        ),
        <.div( // Song & Artist
          ^.cls := "d-flex align-items-center p-2",
          ^.minWidth := "0",
          <.div(
            ^.cls := "h3 text-truncate",
            name,
            <.br,
            <.div(
              ^.cls := "h6 mb-0 text-muted text-truncate",
              artist)
          ))),
      <.div(^.cls := "d-flex align-items-center d-inline-block",
        VoteComp(VoteComp.Props(partyID, song)))
    )
  }

  val Component = ScalaComponent.builder[Props]("PlaylistBox")
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted)
    .build

  def apply(props: Props) = Component(props)
}
