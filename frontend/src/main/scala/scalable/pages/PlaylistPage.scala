package scalable.pages

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

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation._
import scala.scalajs.js.timers._
import scalable.components.{PlaylistBox, Select}
import scalable.config.Config
import scalable.diode.{AppCircuit, _}
import scalable.json.RestService
import scalable.models.{VideoResponse, YoutubeResponse}
import scalable.router.AppRouter

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
                    var selectedData: Option[VideoResponse],
                    //var partyID: String
                  )

  case class YtRequest( key: String,
                        maxResults: Int,
                        part: String,
                        q :String
  )

  class Backend($: BackendScope[Props, State]) extends StrictLogging {

    def mounted: Callback = Callback{
      getData();
    }

    def getData(): Unit ={
      setTimeout(1000) { // note the absence of () =>
        Config.partyId match {
          case Some(id) => RestService.getSongs(id).map{songs =>
            println("Getting Data")
            AppCircuit.dispatch(SetSongsForParty(songs))
          }
          case None => println("NO PARTY ID")
        }
        getData()
      }
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
      val setLoading = $.modState(s => s.copy(isLoading = true))

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
        Config.partyId match {
          case None => println("No Party ID to Put Song into")
          case partyId => {
            s.selectedData match {
              case None => println("no video response")
              case videoResponse => RestService.addSongToParty(partyId.get, videoResponse.get)
            }

          }
        }
        s
      }).runNow()
    }

    def render(p: Props, s: State) = {
      val proxy = p.proxy()
      val select = Select(
        "form-field-name",
        s.selectOptions.toJSArray,
        s.inputValue,
        onInputValueChange,
        onSelectChange,
        pIsLoading = s.isLoading
      )
      <.div(
        <.div("Search for artist or song: "),
        <.div(
          select
        ),
        <.div(
          PlaylistBox(PlaylistBox.Props(p.proxy, p.ctl)
        )
      ))
    }
  }

  val Component = ScalaComponent.builder[Props]("PlaylistPage")
    .initialState(State(
      isLoading = false,
      inputValue = "",
      searchData = List.empty[VideoResponse],
      selectOptions = List.empty[Select.Options],
      selectedData = None : Option[VideoResponse],
      //partyID = "partyID"
    ))
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted)
    .build
}
