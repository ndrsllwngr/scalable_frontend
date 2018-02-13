package weatherApp.components

import scala.concurrent.ExecutionContext.Implicits.global
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra.router.RouterCtl
import org.scalajs.dom
import weatherApp.config.Config
import weatherApp.models._
import weatherApp.router.AppRouter
import io.circe.syntax._
import io.circe.generic.auto._
import weatherApp.diode.{AddCityToFavs, AppCircuit, RemoveCityFromFavs}

object PlaylistBox {

  case class Props (
                     song: Option[SongResponse] = None,
                     ctl: RouterCtl[AppRouter.Page]
                   )

  class Backend(bs: BackendScope[Props, Unit]) {
    val host: String = Config.AppConfig.apiHost

    def addCityForUser(city: OpenWeatherBaseCity, userId: Int, weather: WeatherResponse): Callback = {
      val cityForUser = CityForUser(city, userId).asJson.asInstanceOf[dom.ext.Ajax.InputData]
      Callback {
        dom.ext.Ajax.post(
          url = s"$host/city",
          data = cityForUser,
          headers = Map("Content-Type" -> "application/json")
        ).map(_ => AppCircuit.dispatch(AddCityToFavs(city, weather)))
      }
    }

    def removeCityForUser(city: OpenWeatherBaseCity, userId: Int, weather: WeatherResponse): Callback = {
      val cityForUser = CityForUser(city, userId).asJson.asInstanceOf[dom.ext.Ajax.InputData]
      Callback {
        dom.ext.Ajax.delete(
          url = s"$host/city",
          data = cityForUser,
          headers = Map("Content-Type" -> "application/json")
        ).map(_ => AppCircuit.dispatch(RemoveCityFromFavs(city, weather)))
      }
    }

    def navigateToCityPage(page: AppRouter.CityRoute): Callback = bs.props.flatMap { props =>
      props.ctl.set(page)
    }

    def render(props: Props): VdomElement = {
      <.div(
        props.song.map(song => {
          song.song.map(x => {
            val name = x.name
            val artist = x.artist
            val albumCoverUrl = x.albumCoverUrl
            <.div( // Playlist Row
              ^.cls := "d-flex flex-row justify-content-start bg-white text-dark p-2",
              ^.maxWidth := "800px",
              ^.borderWidth := "2px 0 0 0",
              ^.borderStyle := "solid",
              ^.borderColor := "black",
              <.div(
                ^.cls := "d-flex flex-row justify-content-start mr-auto",
                <.div(
                  ^.cls := "",// AlbumCover
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
                VoteComp())
            )
          }).toVdomArray

        }).whenDefined
      )
    }
  }

  val Component = ScalaComponent.builder[Props]("PlaylistBox")
    .renderBackend[Backend].build

  def apply(props: Props) = Component(props)
}
