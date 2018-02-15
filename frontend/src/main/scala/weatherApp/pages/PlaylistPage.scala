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
import org.scalajs.dom.html
import slogging.StrictLogging
import weatherApp.components.{PlaylistBox, Select}
import weatherApp.diode.{AppCircuit, _}
import weatherApp.models.{Song, VideoResponse, YoutubeResponse}
import weatherApp.router.AppRouter
import weatherApp.json.RestService

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
                     partyID: String,
                     proxy: ModelProxy[AppState],
                     ctl: RouterCtl[AppRouter.Page]
                   )

  case class State(
                    var isLoading: Boolean,
                    var inputValue: String,
                    var searchData: List[VideoResponse],
                    var selectOptions: List[Select.Options],
                    var selectedData: Option[VideoResponse],
                    var partyID: String
                  )

  case class YtRequest( key: String,
                        maxResults: Int,
                        part: String,
                        q :String
  )

  class Backend($: BackendScope[Props, State]) extends StrictLogging {

    def getPlaylist(p: Props) : Future[VdomElement] =  {
      println("testget")
      val songR = RestService.getSongs(p.partyID)
      songR.map(f =>
        <.div(
          PlaylistBox(PlaylistBox.Props(p.partyID,Some(f), p.ctl))
        )
      )
    }

    def triggerRepaint(p: Props) = Callback {
      println("testrepaint")
      val x = getPlaylist(p).map(x =>
          x.renderIntoDOM(dom.document.getElementById("#plBox")))
    }

    def getSelectOptions(data: List[VideoResponse], intputValue: String) = {
      data.zipWithIndex.map { case (item, index) => Select.Options(
        value = s"$intputValue::$index",
        label = s"${item.snippet.title}"
      )}
    }

    def loadSearchResults(searchText: String): Callback = {
      val host = "https://www.googleapis.com/youtube/v3/search"
      val apiKey = "AIzaSyCLQQRT9Qf_rY12nEAS7cc7k5LO1W_qhcg"
      val setLoading = $.modState(s => s.copy(isLoading = true)) // TODO always LOADING

      def getData(): Future[List[VideoResponse]] = {
        val ytData = YtRequest(apiKey, 1, "snippet", searchText).asJson.asInstanceOf[dom.ext.Ajax.InputData]
        logger.debug(ytData.toString)
        Ajax.get(
          url = songSearch(searchText,apiKey,host)
        ).map(xhr => {
          logger.debug(xhr.responseText)
          val option = decode[YoutubeResponse](xhr.responseText)
          logger.debug(option.toString)
          option match {
            case Left(failure) => List.empty[VideoResponse]
            case Right(data) => data.items
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

    def songSearch(name: String, apiKey: String, host: String) = s"$host?key=$apiKey&part=snippet&q=$name"

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
      selectedData = None : Option[VideoResponse],
      partyID = "partyID"
    ))
    .renderBackend[Backend]
    .build
}
