package eft.skyline.domain

import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf

object Data {
 // data to serve to users
  case class CovidData(
                        newConfirmed: Integer,
                        totalConfirmed: Integer,
                        newDeaths: Integer,
                        totalDeaths: Integer,
                        newRecovered: Integer,
                        totalRecovered: Integer
                      )

  case class SummaryData(global: CovidData, perCountry: Map[String, CovidData])

  // converts scala data into json
  implicit val covidDataCodec: Encoder[CovidData] =
    deriveEncoder[CovidData]

  /*
  * @typeparam F[_]: Abstraction over some effect F[_] that may not do I/O
  * */
  implicit def covidData[F[_]]: EntityEncoder[F, CovidData] =
    jsonEncoderOf[F, CovidData]

}
