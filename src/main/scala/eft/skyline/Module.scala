package eft.skyline

import cats.effect.{Concurrent, Timer}
import eft.skyline.config.Config.ApplicationConfig
import eft.skyline.http.CovidDataRoutes
import eft.skyline.service.database.DatabaseService
import org.http4s.{HttpApp, HttpRoutes}
import org.http4s.server.middleware.{AutoSlash, Timeout}
import org.http4s.implicits._


class Module[F[_]: Concurrent: Timer](appConfig: ApplicationConfig, dataService: DatabaseService[F]) {

  type PartialMiddleware = HttpRoutes[F] => HttpRoutes[F]
  type TotalMiddleware = HttpApp[F] => HttpApp[F]



  private val routesMiddleware: PartialMiddleware = {
   { http: HttpRoutes[F] => AutoSlash(http) }
  }

  private val appMiddleware: TotalMiddleware =
    Timeout(appConfig.server.timeout)

  private val http: HttpRoutes[F] = new CovidDataRoutes[F](dataService).routes

  val httpApp: HttpApp[F] = appMiddleware(routesMiddleware(http).orNotFound)
}
