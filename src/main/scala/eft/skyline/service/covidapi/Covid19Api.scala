package eft.skyline.service.covidapi

import cats.effect.Sync
import cats.implicits._
import eft.skyline.config.Config.ApplicationConfig
import eft.skyline.domain.Data
import eft.skyline.domain.Data.SummaryData
import org.http4s.{EntityDecoder, Request, Uri}
import org.http4s.Uri.RegName
import org.http4s.client.Client
import org.http4s.circe._
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import org.http4s.Uri.Scheme.https
/*
* @typeparam F[_]: Abstraction over some Effects
* @typeclass Sync: It defers the computation of some F[_] Effect
* */
class Covid19Api[F[_]: Sync](appConfig: ApplicationConfig, client: Client[F]) extends CovidService[F] {
  import Covid19Api._

  private val covidApiUri: Uri =
    Uri(
      scheme = https.some,
      path = "/summary",
      authority = Uri.Authority(host = RegName(appConfig.api.host), port = appConfig.api.port.some).some
    )

  private val covid19ApiRequest: Request[F] = Request[F](
    uri = covidApiUri
  )

  private val getCovid19Data: F[Covid19ApiData] =
    client.expect(covid19ApiRequest)

  override def requestGlobalData(): F[Data.SummaryData] =
    getCovid19Data.map { data =>
      val global = Covid19Api.covidDataToDomainData(data.Global)
      val perCountry = data.Countries.map(Covid19Api.covidCountryDataToDomainData).toMap
      SummaryData(global, perCountry)
    }
    
}

object Covid19Api {
  // converts raw data into business logic(app)
  def covidDataToDomainData(data: CovidData): Data.CovidData = Data.CovidData(
    newConfirmed = data.NewConfirmed,
    totalConfirmed = data.TotalConfirmed,
    newDeaths = data.NewDeaths,
    totalDeaths = data.TotalDeaths,
    newRecovered = data.NewRecovered,
    totalRecovered = data.TotalRecovered
  )

  def covidCountryDataToDomainData(data: CovidCountryData): (String, Data.CovidData) = data.Country.toLowerCase -> Data.CovidData(
    newConfirmed = data.NewConfirmed,
    totalConfirmed = data.TotalConfirmed,
    newDeaths = data.NewDeaths,
    totalDeaths = data.TotalDeaths,
    newRecovered = data.NewRecovered,
    totalRecovered = data.TotalRecovered
  )

// raw data for api
  case class CovidData(NewConfirmed: Integer,
                        TotalConfirmed: Integer,
                        NewDeaths: Integer,
                        TotalDeaths: Integer,
                        NewRecovered: Integer,
                        TotalRecovered: Integer)

  case class CovidCountryData(NewConfirmed: Integer,
                              TotalConfirmed: Integer,
                              NewDeaths: Integer,
                              TotalDeaths: Integer,
                              NewRecovered: Integer,
                              TotalRecovered: Integer,
                              Country: String)

  case class Covid19ApiData(Global: CovidData, Countries: List[CovidCountryData])

  // converts json into reasonable scala datatype
  implicit def entityDataDecoder[F[_]: Sync]: EntityDecoder[F, Covid19ApiData] =
    jsonOf[F, Covid19ApiData]
  implicit val countryDataDecoder: Decoder[CovidCountryData] =
    deriveDecoder[CovidCountryData]
  implicit val globalDataDecoder: Decoder[CovidData] =
    deriveDecoder[CovidData]
  implicit val dataDecoder: Decoder[Covid19ApiData] =
    deriveDecoder[Covid19ApiData]

  // creates instance of covid service
  def apply[F[_]: Sync](appConfig: ApplicationConfig, client: Client[F]): CovidService[F] =
    new Covid19Api[F](appConfig, client)

}