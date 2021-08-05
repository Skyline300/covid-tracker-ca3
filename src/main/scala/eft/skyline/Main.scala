package eft.skyline

import cats.effect._
import cats.effect.concurrent.Ref
import eft.skyline.config.Config.ApplicationConfig
import eft.skyline.domain.Data.{CovidData, SummaryData}
import eft.skyline.service.covidapi.{Covid19Api, CovidService}
import eft.skyline.service.database.{DatabaseService, IORefService}
import org.http4s.client.Client
import fs2.Stream
import cats.implicits._
import eft.skyline.config.Config
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.server.blaze.BlazeServerBuilder

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

object Main extends IOApp {
  // initializes everything
  private def createStore(): IO[Ref[IO, SummaryData]] = {
    val covidDataDefault = CovidData(0, 0, 0, 0, 0, 0)
    val covidCountryDataMap = Map.empty[String, CovidData]
    val initialSummaryData = SummaryData(covidDataDefault, covidCountryDataMap)
    Ref.of[IO, SummaryData](initialSummaryData)
  }

  override def run(args: List[String]): IO[ExitCode] = BlazeClientBuilder[IO](executionContext).resource.use { client =>
    for {
      store <- createStore()
      appStream = for {
        config <- Config.read[IO]("app")
        app = new Application[IO](config, client, store)
        foreverRetrieveData = app.scheduleDataRetrieval().foreverM
        _ <- app.startServer(executionContext).mergeHaltL(foreverRetrieveData)
      } yield ()
      _ <- appStream.compile.drain
    } yield ExitCode.Success
  }
}

// initialized on line 33
class Application[F[_]: ConcurrentEffect: Timer](appConfig: ApplicationConfig, client: Client[F], storeRef: Ref[F, SummaryData]) {

  private val storeService: DatabaseService[F] = IORefService(storeRef)
  private val apiService: CovidService[F] = Covid19Api(appConfig, client)

  def scheduleDataRetrieval(): Stream[F, Unit] =
    Stream.eval(retrieveAndUpdateCovidData()) *> Stream.sleep(5.minute)

  def startServer(ec: ExecutionContext): Stream[F, Unit] = {
    val module = new Module[F](appConfig, storeService)
    BlazeServerBuilder[F](ec)
      .bindHttp(appConfig.server.port, appConfig.server.host)
      .withHttpApp(module.httpApp)
      .serve.as(())
  }

  private def retrieveAndUpdateCovidData(): F[Unit] =
  (for {
    data <- apiService.requestGlobalData()
    _ <- storeService.setCovidData(data)
  } yield ()).attempt.flatMap {
    case Left(_) =>
      retrieveAndUpdateCovidData()
    case _ => Sync[F].unit
  }

}