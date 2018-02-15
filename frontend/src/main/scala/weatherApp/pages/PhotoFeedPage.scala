package weatherApp.pages

import diode.react.ModelProxy
import firebase.{Firebase, FirebaseConfig}
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^.{<, ^, _}
import japgolly.scalajs.react.{BackendScope, Callback, ReactEventFromInput, ScalaComponent}
import org.scalajs.dom.html
import org.scalajs.dom.html.Div
import weatherApp.config.Config
import weatherApp.diode.AppState
import weatherApp.router.AppRouter

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport


object PhotoFeedPage {


  @js.native
  @JSImport("lodash.throttle", JSImport.Default)
  private object _throttle extends js.Any

  def throttle: js.Dynamic = _throttle.asInstanceOf[js.Dynamic]


  case class Props(
                    proxy: ModelProxy[AppState],
                    roomCode: String,
                    ctl: RouterCtl[AppRouter.Page]
                  )


  class Backend(bs: BackendScope[Props, Unit]) {
    val host: String = Config.AppConfig.apiHost

    var fileChooser : html.Input = _

    val apiKey = "AIzaSyC8vZ20nRwOpSmuyF0TjimoHHqSxkWK4cE"
    val  authDomain = "scalable-195120.firebaseapp.com"
    val  databaseURL= "https://scalable-195120.firebaseio.com"
    val  projectId = "scalable-195120"
    val  storageBucket = ""
    val  messagingSenderId = "547307244060"
    val config = FirebaseConfig(apiKey, authDomain, databaseURL, storageBucket, messagingSenderId)
    Firebase.initializeApp(config, "scalable")
    val app = Firebase.app("scalable")

    def choosePhoto(): Callback = {
      Callback.alert("choosePhoto")
    }


    def publishLink(url: String): Unit ={

    }

    def onPhotoChanged(props: Props) (e: ReactEventFromInput) = Callback {
      val choosenFile = e.target.files.item(0)
      val storage = Firebase.storage(app).refFromURL(s"gs://scalable-195120.appspot.com/${props.roomCode}/${choosenFile.name}")
      storage.put(choosenFile).then(success => {
        publishLink(success.downloadURL.toString)
      }, reject => {
        Firebase.database(app).ref(props.roomCode).push("error", completed => {})
      })

      js.undefined
    }

    def render(p: Props): VdomTagOf[Div] = {
      val proxy = p.proxy()
      <.div(^.cls := "form-group",
        <.label("Fotofeed"),
        <.div(
          <.input(^.`type` := "file", ^.cls := "form-control", ^.id := "files",
            ^.onChange ==> onPhotoChanged(p))
          .ref(fileChooser = _)

        )
      )
    }

  }

  val Component = ScalaComponent.builder[Props]("PhotoFeedPage")
    .renderBackend[Backend]
    .build

}
