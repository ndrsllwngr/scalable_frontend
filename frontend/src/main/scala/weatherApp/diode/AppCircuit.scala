package weatherApp.diode

import diode._
import diode.react.ReactConnector
import weatherApp.models.{UserResponse, VideoResponse, WeatherForecastResponse, WeatherResponse}

object AppCircuit extends Circuit[AppModel] with ReactConnector[AppModel] {
  def initialModel = AppModel(
    AppState(
      weatherSuggestions = List.empty[WeatherResponse],
      videoSuggestions = List.empty[VideoResponse],
      forecast = None: Option[WeatherForecastResponse],
      selectedWeather = None: Option[WeatherResponse],
      selectedVideo = None: Option[VideoResponse],
      isLoading = false,
      userInfo = None : Option[UserResponse],
      favCitiesWeather = List.empty[WeatherResponse]
    )
  )

  override val actionHandler = composeHandlers(
    new PlaylistPageHandler(zoomTo(_.state)),
    new WeatherPageHandler(zoomTo(_.state)),
    new AppHandler(zoomTo(_.state))
  )
}



class PlaylistPageHandler[M](modelRW: ModelRW[M, AppState]) extends ActionHandler(modelRW) {
  override def handle = {
    case GetVideoSuggestions(videoSuggestions) => updated(value.copy(videoSuggestions = videoSuggestions))
    case SelectVideo(video) => updated(value.copy(selectedVideo = video))
    }
  }


class WeatherPageHandler[M](modelRW: ModelRW[M, AppState]) extends ActionHandler(modelRW) {
  override def handle = {
    case GetWeatherSuggestions(weatherSuggestions) => updated(value.copy(weatherSuggestions = weatherSuggestions))
    case GetVideoSuggestions(videoSuggestions) => updated(value.copy(videoSuggestions = videoSuggestions))
    case GetWeatherForecast(forecast) => updated(value.copy(forecast = forecast))
    case ClearForecast() => updated(value.copy(forecast = None))
    case SelectWeather(weather) => updated(value.copy(selectedWeather = weather))
    case SelectVideo(video) => updated(value.copy(selectedVideo = video))
    case GetWeatherForFavCity(weather) => updated(value.copy(favCitiesWeather = value.favCitiesWeather ++ List(weather)))
    case AddCityToFavs(city, weather) => {
      updated(value.userInfo.fold(value) {userInfo =>
        val newUserInfo = userInfo.copy(cities = userInfo.cities ++ List(city))
        value.copy(userInfo = Some(newUserInfo), favCitiesWeather = value.favCitiesWeather ++ List(weather))
      })
    }
    case RemoveCityFromFavs(city, weather) => {
      updated(value.userInfo.fold(value) {userInfo =>
        val newUserInfo = userInfo.copy(cities = userInfo.cities.filter(_.id != city.id))
        value.copy(userInfo = Some(newUserInfo), favCitiesWeather = value.favCitiesWeather.filter(_.id != weather.id))
      })
    }
  }
}

class AppHandler[M](modelRW: ModelRW[M, AppState]) extends ActionHandler(modelRW) {
  override def handle = {
    case SetLoadingState() => updated(value.copy(isLoading = true))
    case ClearLoadingState() => updated(value.copy(isLoading = false))
    case GetUserInfo(userInfo) => updated(value.copy(userInfo = userInfo))
  }
}
