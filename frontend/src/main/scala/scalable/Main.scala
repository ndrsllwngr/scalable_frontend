package scalable

import firebase.{Firebase, FirebaseConfig}
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
    val apiKey = "AIzaSyC8vZ20nRwOpSmuyF0TjimoHHqSxkWK4cE"
    val authDomain = "scalable-195120.firebaseapp.com"
    val databaseURL = "https://scalable-195120.firebaseio.com"
    val projectId = "scalable-195120"
    val storageBucket = ""
    val messagingSenderId = "547307244060"
    val appName = "scalable"
    val config = FirebaseConfig(apiKey, authDomain, databaseURL, storageBucket, messagingSenderId)
    Firebase.initializeApp(config, appName)
    val target = dom.document.getElementById("target")
    AppRouter.router().renderIntoDOM(target)
  }
}
