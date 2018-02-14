package weatherApp.pages

import diode.react.ModelProxy
import io.circe.generic.auto._
import io.circe.parser.decode
import io.circe.syntax._
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom
import org.scalajs.dom.ext.Ajax
import slogging.StrictLogging
import weatherApp.components.{PlaylistBox, Select}
import weatherApp.diode.{AppCircuit, _}
import weatherApp.models.{Song, SongResponse, VideoResponse}
import weatherApp.router.AppRouter

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation._

object PlaylistPage {


  @js.native
  @JSImport("lodash.throttle", JSImport.Default)
  private object _throttle extends js.Any
  def throttle: js.Dynamic = _throttle.asInstanceOf[js.Dynamic]


  case class Props (
                     proxy: ModelProxy[AppState],
                     ctl: RouterCtl[AppRouter.Page]
                   )

  case class State(
                    var isLoading: Boolean,
                    var inputValue: String,
                    var searchData: List[VideoResponse],
                    var selectOptions: List[Select.Options],
                    var selectedData: Option[VideoResponse]
                  )

  case class YtRequest( key: String,
                        maxResults: Int,
                        part: String,
                        q :String
  )

  class Backend($: BackendScope[Props, State]) extends StrictLogging {
    def getSelectOptions(data: List[VideoResponse], intputValue: String) = {
      data.zipWithIndex.map { case (item, index) => Select.Options(
        value = s"$intputValue::$index",
        label = s"${item.snippet.title}, ${item.snippet.channelTitle} ${item.snippet.description}"
      )}
    }

    def loadSearchResults(searchText: String): Callback = {
      val host = "https://www.googleapis.com/youtube/v3/search"
      val apiKey = "AIzaSyCLQQRT9Qf_rY12nEAS7cc7k5LO1W_qhcg"
      val setLoading = $.modState(s => s.copy(isLoading = true))

      def getData(): Future[List[VideoResponse]] = {
        val ytData = YtRequest(apiKey, 2, "snippet", "Howling").asJson.asInstanceOf[dom.ext.Ajax.InputData]
        Callback.alert(ytData.toString)
        logger.debug(ytData.toString)
        Ajax.get(
          url = host,
          data = ytData
        ).map(xhr => {
          logger.debug(xhr.responseText)
          val option = decode[List[VideoResponse]](xhr.responseText)
          option match {
            case Left(failure) => List.empty[VideoResponse]
            case Right(data) => data
          }
        })
      }

      def updateState: Future[Callback] = {
        getData().map {result =>
          AppCircuit.dispatch(GetVideoSuggestions(result))
          $.modState(s => s.copy(
            isLoading =  false,
            searchData = result,
            selectOptions = getSelectOptions(result, s.inputValue))
          )
        }
      }

      setLoading >> Callback.future(updateState)
    }

    val throttleInputValueChange: js.Dynamic = {
      throttle(() => {
        $.state.map { state =>
          val city = state.inputValue
          if (city.nonEmpty) {
            loadSearchResults(city).runNow()
          }
        }.runNow()
      }, 400)
    }

    def onInputValueChange(value: String): Unit = {
      val selectedValue = try {
        Some(value)
      } catch {
        case e: Exception => None : Option[String]
      }
      $.modState(s => s.copy(inputValue = selectedValue.getOrElse(""))).runNow()
      throttleInputValueChange()
    }

    def onSelectChange(option: Select.Options) = {
      val selectedValue = try {
        Some(option.value)
      } catch {
        case e: Exception => None : Option[String]
      }
      $.modState(s => {
        s.inputValue = selectedValue.getOrElse("")
        if (s.inputValue == "") {
          s.selectOptions = List.empty[Select.Options]
          s.selectedData = None: Option[VideoResponse]

        } else {
          val arr = option.value.split("::")
          val index = if (arr.length == 2) arr(1).toInt else -1
          s.selectedData = if (index == -1) None else Some(s.searchData(index))
        }
        AppCircuit.dispatch(SelectVideo(s.selectedData))
        s
      }).runNow()
    }

    def render(p: Props, s: State) = {
      val proxy = p.proxy()
      val weatherData = proxy.weatherSuggestions
      val userInfo = proxy.userInfo
      val select = Select(
        "form-field-name",
        s.selectOptions.toJSArray,
        s.inputValue,
        onInputValueChange,
        onSelectChange,
        pIsLoading = s.isLoading
      )
      <.div(
        ^.margin := "0 auto",
        ^.className := "weather-page",
        <.div(
          ^.className := "weather-page__label",
          "Search for artist or song: "
        ),
        <.div(
          ^.marginBottom := 10.px,
          select
        ),
        <.div(
          PlaylistBox(PlaylistBox.Props("bla",Some(SongResponse(1,List(Song(864,"streamingService!","Fineshrine","Purity Ring","Shrines","https://i.scdn.co/image/0beb85a35a4ef3242432207f1a323151db693bce",5,1,false),
            Song(865,"streamingService!","Howling","RY X","Dawn","https://i.scdn.co/image/df4dd74119df85d052c0a3423cadca459a8331c1",3,3,false),
            Song(866,"streamingService!","Spectyrum (Say My Name) - Calvin Harris Remix","Florence + The Machine","None","https://i.scdn.co/image/75c1be006328c8b1888b29728deec0f455ac8207",0,1,false)
          ))), p.ctl))
        )
      )
    }
  }

  val Component = ScalaComponent.builder[Props]("PlaylistPage")
    .initialState(State(
      isLoading = false,
      inputValue = "",
      searchData = List.empty[VideoResponse],
      selectOptions = List.empty[Select.Options],
      selectedData = None : Option[VideoResponse]
    ))
    .renderBackend[Backend]
    .build
}
