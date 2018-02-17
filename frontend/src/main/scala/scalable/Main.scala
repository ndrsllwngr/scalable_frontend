package scalable

import firebase.{Firebase, FirebaseConfig}
import japgolly.scalajs.react.ReactDOM
import japgolly.scalajs.react.WebpackRequire.React
import org.scalajs.dom

import scalable.router.AppRouter

object Main {

  def require(): Unit = {
    React
    ReactDOM
  }

  def main(args: Array[String]): Unit = {
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
