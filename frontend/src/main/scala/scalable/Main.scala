package scalable

import japgolly.scalajs.react.ReactDOM
import japgolly.scalajs.react.WebpackRequire.React
import org.scalajs.dom
import slogging.{LogLevel, LoggerConfig, PrintLoggerFactory}

import scala.scalajs.js
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation._
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
