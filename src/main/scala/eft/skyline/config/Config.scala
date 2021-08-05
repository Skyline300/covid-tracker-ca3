package eft.skyline.config

import cats.effect.Sync
import fs2.Stream
import pureconfig.ConfigSource
import pureconfig.generic.auto._

import scala.concurrent.duration.FiniteDuration

object Config {

  case class ApplicationConfig(api: CovidApiUri, server: ApplicationUri)

  case class CovidApiUri(host: String, port: Int)

  case class ApplicationUri(host: String, port: Int, timeout: FiniteDuration)


  /*
  * @typeparam F[_]: Abstraction over some Effects
  * @typeclass Sync: It defers the computation of some F[_] Effect
  * @type Stream: Sequence of effects on F[_] that conserves memory usage
  * */
  // file reading from application.conf
  def read[F[_]: Sync](path: String): Stream[F, ApplicationConfig] =
    Stream.eval(Sync[F].delay(
      ConfigSource.default.at(path).loadOrThrow[ApplicationConfig]
    ))
}
