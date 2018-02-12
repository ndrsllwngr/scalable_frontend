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
            <.div(
              ^.cls := "weather-box",
              ^.maxWidth := "800px",
              ^.display := "flex",
              ^.border := "1px solid",
              ^.color := "black",
              <.div(
                ^.margin := "5px",
                ^.display := "flex",
                ^.flexDirection := "row",
                ^.justifyContent := "space-between",
                ^.width := "100%",
                ^.fontSize := 15.px,
                <.div(
                  <.div(name)
                ),
                <.div(
                  <.div(artist)
                ),
                <.div(
                  ^.display := "flex",
                  ^.justifyContent := "center",
                  ^.alignItems := "center",
                  <.div(
                    ^.className := "",
                    ^.marginRight := "10px",
                    ^.fontSize := 25.px,
                    ^.fontWeight := "100"
                  ),
                  <.div(
                  )
                )
              ),
              <.div(
                ^.display := "flex",
                ^.flexDirection := "row",
                ^.justifyContent := "center",
                ^.alignItems := "center",
                ^.padding := 5.px,
                WeatherBoxBtn(
                  WeatherBoxBtn.Props(
                    "more",
                    navigateToCityPage(AppRouter.CityRoute("munich", 2867714))
                  )
                ), <.div(VoteComp())
              )
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
