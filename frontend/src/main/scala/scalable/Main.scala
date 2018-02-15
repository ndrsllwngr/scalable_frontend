package scalable

import japgolly.scalajs.react.WebpackRequire.React
import org.scalajs.dom

import scala.scalajs.js.JSApp
import scalajs.js
import scalajs.js.annotation._
import japgolly.scalajs.react.{ReactDOM, WebpackRequire}
import japgolly.scalajs.react.vdom.html_<^._
import slogging.{LogLevel, LoggerConfig, PrintLoggerFactory}
import scalable.router.AppRouter

object Main extends JSApp {

  LoggerConfig.factory = PrintLoggerFactory()

  // set log level, e.g. to DEBUG
  LoggerConfig.level = LogLevel.DEBUG

  @JSImport("normalize.css", JSImport.Namespace)
  @js.native
  object Normalize extends js.Any

  def require(): Unit = {
    React
    ReactDOM
    Normalize
  }

  override def main(): Unit = {
    require()

    val target = dom.document.getElementById("target")
    AppRouter.router().renderIntoDOM(target)
  }
}
