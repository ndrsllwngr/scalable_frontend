package scalable.components

import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{BackendScope, ScalaComponent}

import scalable.config.Config
import scalable.models._
import scalable.router.AppRouter

object PhotoFeedBox {

  case class Props (
                     partyID: String,
                     feed: Option[List[PhotoReturn]] = None,
                     ctl: RouterCtl[AppRouter.Page]
                   )

  class Backend(bs: BackendScope[Props, Unit]) {
    val host: String = Config.AppConfig.apiHost


    def render(props: Props): VdomElement = {
      props.feed match {
        case None => <.div()
        case Some(feed) => <.div(feed.map(x => {
          val imageUrl = x.url
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
                  ^.maxWidth := 200.px,
                  ^.src := imageUrl
                )
              ),
            <.div(^.cls := "d-flex align-items-center d-inline-block"
             // VoteComp(VoteComp.Props(props.partyID, Some(x.id))))
            )))
        }).toVdomArray)
      }
    }
  }

  val Component = ScalaComponent.builder[Props]("PhotoFeedBox")
    .renderBackend[Backend].build

  def apply(props: Props) = Component(props)

}
