package scalable

import firebase.{Firebase, FirebaseConfig}
import japgolly.scalajs.react.ReactDOM
import japgolly.scalajs.react.WebpackRequire.React
import org.scalajs.dom

import scalable.services.FirebaseService
import scalable.router.AppRouter

object Main {

  def require(): Unit = {
    React
    ReactDOM
  }

  def main(args: Array[String]): Unit = {
    require()
    FirebaseService.init()
    val target = dom.document.getElementById("target")
    AppRouter.router().renderIntoDOM(target)
  }
}
