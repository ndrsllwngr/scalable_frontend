package weatherApp.router

import japgolly.scalajs.react.extra.router._
import japgolly.scalajs.react.vdom.html_<^._
import weatherApp.components.Layout
import weatherApp.diode.AppCircuit
import weatherApp.pages._

object AppRouter {
  sealed trait Page
  case object HomeRoute extends Page
  case class CityRoute(name: String, id: Int) extends Page
  case object FavoritesRoute extends Page
  case object RoomRoute extends Page
  case class AdminRoute(roomCode: String) extends Page

  val connection = AppCircuit.connect(_.state)

  val routerConfig = RouterConfigDsl[Page].buildConfig { dsl =>
    import dsl._
    (trimSlashes
      | staticRoute(root, HomeRoute) ~> renderR(renderWeatherPage)
      | staticRoute("favorites", FavoritesRoute) ~> renderR(renderFavoritesPage)
      | staticRoute("rooms", RoomRoute) ~> renderR(renderRoomPage)
      | dynamicRouteCT(("city" / string(".*") / int).caseClass[CityRoute]) ~> dynRenderR(renderCityPage)
      | dynamicRouteCT(("admin"/ string(".*")).caseClass[AdminRoute]) ~> dynRenderR(renderAdminPage)
    )
      .notFound(redirectToPage(HomeRoute)(Redirect.Replace))
      .renderWith(layout)
  }

  def renderWeatherPage(ctl: RouterCtl[Page]) = {
    connection(proxy => WeatherPage.Component(WeatherPage.Props(proxy, ctl)))
  }

  def renderRoomPage(ctl: RouterCtl[Page]) = {
    connection(proxy => RoomPage.Component(RoomPage.Props(proxy, ctl)))
  }

  def renderAdminPage(p: AdminRoute, ctl: RouterCtl[Page]) = {
    connection(proxy => AdminPage.Component(AdminPage.Props(proxy, p.roomCode, ctl)))
  }

  def renderCityPage(p: CityRoute, ctl: RouterCtl[Page]) = {
    connection(proxy => CityPage.Component(CityPage.Props(proxy, p.id, p.name, ctl)))
  }

  def renderFavoritesPage(ctrl: RouterCtl[Page]) = {
    connection(proxy => FavoritesPage(FavoritesPage.Props(proxy, ctrl)))
  }

  def layout (c: RouterCtl[Page], r: Resolution[Page]) = connection(proxy => Layout(Layout.Props(proxy, c, r)))

  val baseUrl = BaseUrl.fromWindowOrigin_/

  val router = Router(baseUrl, routerConfig.logToConsole)
}
