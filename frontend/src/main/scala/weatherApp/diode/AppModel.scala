package weatherApp.diode

import diode.Action
import weatherApp.models._

case class AppState (
                    partyId:Option[String],
                    songList:List[Song],
                      weatherSuggestions: List[WeatherResponse],
                      videoSuggestions: List[VideoResponse],
                      forecast: Option[WeatherForecastResponse],
                      selectedWeather: Option[WeatherResponse],
                      selectedVideo: Option[VideoResponse],
                      isLoading: Boolean,
                      userInfo: Option[UserResponse],
                      favCitiesWeather: List[WeatherResponse]
                    )

case class AppModel(
                     state: AppState
                   )


//TODO
case class SetPartyId(partyId: String) extends Action

case class SetSongsForParty(songList: List[Song]) extends Action

//TODO
case class GetWeatherSuggestions(suggestions: List[WeatherResponse]) extends Action

case class GetVideoSuggestions(suggestions: List[VideoResponse]) extends Action

case class GetWeatherForecast(forecast: Option[WeatherForecastResponse]) extends Action

case class ClearForecast() extends Action

case class SelectWeather(weather: Option[WeatherResponse]) extends Action

case class SelectVideo(video: Option[VideoResponse]) extends Action

case class SetLoadingState() extends Action

case class ClearLoadingState() extends Action

case class GetUserInfo(userInfo: Option[UserResponse]) extends Action

case class GetWeatherForFavCity(weather: WeatherResponse) extends Action

case class AddCityToFavs(city: OpenWeatherBaseCity, weather: WeatherResponse) extends Action

case class RemoveCityFromFavs(city: OpenWeatherBaseCity, weather: WeatherResponse) extends Action

case class AddSongForParty(partyID: String,
                           song: Song) extends Action

case class VoteSongForParty(partyID: String,
                            songID: Long,
                            positive:Boolean) extends Action

case class CreateParty(partyName: String) extends Action
