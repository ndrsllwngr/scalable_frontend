package scalable.pages

import diode.react.ModelProxy
import firebase.Firebase
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^.{<, ^, _}
import japgolly.scalajs.react.{BackendScope, Callback, ReactEventFromInput, ScalaComponent}
import org.scalajs.dom.html
import org.scalajs.dom.html.Div

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scala.scalajs.js.timers.SetIntervalHandle
import scalable.components.PhotoFeedBox
import scalable.config.Config
import scalable.diode.{AppCircuit, AppState, SetPhotosForParty}
import scalable.json.RestService
import scalable.models.PhotoReturn
import scalable.router.AppRouter


object PhotoFeedPage {


  @js.native
  @JSImport("lodash.throttle", JSImport.Default)
  private object _throttle extends js.Any

  def throttle: js.Dynamic = _throttle.asInstanceOf[js.Dynamic]


  case class Props(
                    proxy: ModelProxy[AppState],
                    ctl: RouterCtl[AppRouter.Page]
                  )


  class Backend(bs: BackendScope[Props, Unit]) {
    val host: String = Config.AppConfig.apiHost

    var fileChooser: html.Input = _
    var photoFeed: html.Div = _

    val app = Firebase.app("scalable")

    var timer: SetIntervalHandle = _

    def mounted: Callback = Callback {
      getData()
      startUpdateInterval()
    }

    def startUpdateInterval(): Unit ={
      timer = js.timers.setInterval(10000) { // note the absence of () =>
        getData()
      }
    }

    def getData(): Unit ={
        Config.partyId match {
          case Some(id) => RestService.getPhotos(id).map { photos =>
            println("Getting Data")
            AppCircuit.dispatch(SetPhotosForParty(photos))
          }
          case None => println("NO PARTY ID")
        }

    }


    def unmounted: Callback = Callback {
      println("Unmounted")
      js.timers.clearInterval(timer)
    }



    def publishLink(url: String, roomCode: String): Unit = {
      RestService.addPhoto(url, roomCode).onComplete(_ => getData())
    }

    def onPhotoChanged()(e: ReactEventFromInput) = Callback {
      val choosenFile = e.target.files.item(0)
      Config.partyId match {
        case Some(id) =>
          val storage = Firebase.storage(app).refFromURL(s"gs://scalable-195120.appspot.com/$id/${choosenFile.name}")
          storage.put(choosenFile).then(success => {
            publishLink(success.downloadURL.toString, id)
          }, reject => {
            println("Upload Failed")
          })
        case None => println("NO PARTY ID")
      }
      js.undefined
    }

    def getPhotofeed(partyId: String): Future[List[PhotoReturn]] = {
      RestService.getPhotos(partyId)
    }

    def render(p: Props): VdomTagOf[Div] = {
      val proxy = p.proxy()

      <.div(^.cls := "form-group",
        <.label("Fotofeed"),
        <.div(
          <.input(^.`type` := "file", ^.cls := "form-control", ^.id := "files",
            ^.onChange ==> onPhotoChanged())
            .ref(fileChooser = _)
        ), <.div(
          PhotoFeedBox(PhotoFeedBox.Props(p.proxy, p.ctl))
        )
      )
    }

  }

  val Component = ScalaComponent.builder[Props]("PhotoFeedPage")
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted)
    .componentWillUnmount(scope => scope.backend.unmounted)
    .build

}
