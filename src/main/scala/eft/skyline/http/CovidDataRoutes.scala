package eft.skyline.http

import cats.effect.Sync
import eft.skyline.service.database.DatabaseService
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import cats.implicits._
import eft.skyline.domain.Data.{CovidData, SummaryData}
import org.http4s.server.Router

/*
* @typeparam F[_]: Abstraction over some Effects
* @typeclass Sync: It defers the computation of some F[_] Effect
* */
class CovidDataRoutes[F[_]: Sync](databaseService: DatabaseService[F]) extends Http4sDsl[F] {

  private val prefixPath = "/api/v1"

  private val httpRoutes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root =>
      databaseService.getGlobalCovidData().flatMap { covidData =>
        Ok(covidData)
      }

    case GET -> (Root / country) =>
      databaseService.getCountryCovidData(country.toLowerCase).flatMap {
          case Some(covidData) => Ok(covidData)
          case None => NotFound(s"Country \"$country\" does not exist")
      }


    case DELETE -> Root =>
      val initCovidData = CovidData(0, 0, 0, 0, 0, 0)
      val initSummaryData = SummaryData(initCovidData, Map.empty)
      for {
        _ <- databaseService.setCovidData(initSummaryData)
        data <- databaseService.getGlobalCovidData()
        res <- Ok(data)
      } yield res
  }

  val routes: HttpRoutes[F] =
    Router(prefixPath -> httpRoutes)

}
