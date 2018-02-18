package scalable.components

import diode.react.ModelProxy
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{BackendScope, Callback, ScalaComponent}

import scalable.diode.AppState
import scalable.models._
import scalable.router.AppRouter

object PhotoFeedBox {

  case class Props (
                     proxy: ModelProxy[AppState],
                     onVoted : Event => Unit,
                     ctl: RouterCtl[AppRouter.Page],
                     admin: Boolean
                   )

  class Backend(bs: BackendScope[Props, Unit]) {
    def mounted: Callback = Callback.log("Mounted!")

    def render(props: Props): VdomElement = {
      <.div(getFeed(props).toVdomArray)
    }
  }

  def getFeed(props: Props) ={
    val proxy                        = props.proxy()
    val feed                        = proxy.photoFeed
    val partyId                      = proxy.partyId
    partyId match {
      case Some(id) => feed.map(x => {
        photoView(x,id, props)
      })
      case None => Seq(<.p("No party ID set"))
    }

  }

  def photoView(photo: PhotoReturn, partyID:String ,props: Props) ={
    val customStyle = VdomStyle("background-image")
    val id = photo.id
    val url = photo.url
    <.div( // Playlist Row (Parent)
      ^.cls := "d-flex flex-row align-items-center bg-white text-dark p-2",
      ^.maxWidth := 800.px,
      ^.borderWidth := "2px 0 0 0",
      ^.borderStyle := "solid",
      ^.borderColor := "black",
      <.div( // Child 1 AlbumCover
        ^.cls := "mr-2",
        ^.flex := "0 0 auto",
        ^.width := 500.px,
        ^.height := 500.px,
        ^.backgroundClip := "padding-box",
        ^.backgroundImage := s"url($url)",
        ^.backgroundSize := "cover",
        ^.backgroundPosition := "center center"
      ),
      <.div( // Child 3 VoteComp
        ^.flex := "0 0 auto",
        VoteComp(VoteComp.Props(VoteAble(partyID = partyID, compId = photo.id, voteType = "PHOTO" ,upvotes = photo.upvotes, downvotes = photo.downvotes), props.onVoted, admin = props.admin)))
    )
  }

  val Component = ScalaComponent.builder[Props]("PhotoFeedBox")
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted)
    .build

  def apply(props: Props) = Component(props)

}
