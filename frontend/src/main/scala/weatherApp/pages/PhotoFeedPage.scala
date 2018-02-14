package weatherApp.pages

import chandu0101.scalajs.react.components.WithAsyncScript
import chandu0101.scalajs.react.components.elementalui.FileUpload
import diode.react.ModelProxy
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^.{<, ^, _}
import japgolly.scalajs.react.{BackendScope, Callback, ReactEvent, ScalaComponent}
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


    def choosePhoto(): Callback = {
      Callback.alert("choosePhoto")
    }

    def onPhotoChanged(): js.UndefOr[ReactEvent => Callback] = {
      js.undefined
    }

    def render(p: Props): VdomTagOf[Div] = {
      val proxy = p.proxy()
      val className = js.UndefOr.any2undefOrA("image")
      val accept = js.UndefOr.any2undefOrA("upload")
      val buttonLabelChange = js.UndefOr.any2undefOrA("change")
      val buttonLabelInitial = js.UndefOr.any2undefOrA("choose")
      val file = js.undefined

      <.div(^.cls := "form-group",
        <.label("Fotofeed"),

        <.div(
          WithAsyncScript("/elemental_ui-bundle.js")(
            FileUpload(className, accept, buttonLabelChange, buttonLabelInitial, file, onChange= onPhotoChanged()).apply()
          )
        )
      )
    }

  }

  val Component = ScalaComponent.builder[Props]("PhotoFeedPage")
    .renderBackend[Backend]
    .build

}
