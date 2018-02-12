package weatherApp.components

import japgolly.scalajs.react.{Children, JsFnComponent}

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSImport, ScalaJSDefined}

object RoomCodeBox {
  @JSImport("react-select", JSImport.Default)
  @js.native
  object JsComp extends js.Any

  @JSImport("react-select/dist/react-select.css", JSImport.Namespace)
  @js.native
  object CSS extends js.Any
  CSS

  @ScalaJSDefined
  trait Options extends js.Object {
    val value: String
    val label: String
  }

  object Options {
    def apply(value: String, label: String) =  js.Dynamic.literal(value = value, label = label).asInstanceOf[Options]
  }

  @ScalaJSDefined
  trait Props extends js.Object {
    val name: String
    val value: String
    val isLoading: Boolean
    val backspaceRemoves: Boolean
  }

  val Component = JsFnComponent[Props, Children.Varargs](JsComp)

  def props(
             pName: String,
             pValue: String,
             pIsLoading: Boolean = false
           ): Props = {
    new Props {
      val name = pName
      val value = pValue
      val isLoading = pIsLoading
      val backspaceRemoves = false
    }
  }

  def apply(    pName: String,
                pValue: String,
                pIsLoading: Boolean = false
           ) = Component(props(pName, pValue, pIsLoading))()
}
