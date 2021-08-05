package eft.skyline.service.database

import cats.effect.Sync
import cats.effect.concurrent.Ref
import cats.implicits._
import eft.skyline.domain.Data.{CovidData, SummaryData}

/*
* @typeparam: F[_]: Abstraction for an Effect type that may or may not do I/O
* */
class IORefService[F[_]: Sync](storeRef: Ref[F, SummaryData]) extends DatabaseService[F]{
  override def setCovidData(data: SummaryData): F[Unit] =
    storeRef.update(_ => data)

  override def getGlobalCovidData(): F[CovidData] =
    storeRef.get.map(_.global)

  override def getCountryCovidData(country: String): F[Option[CovidData]] =
    storeRef.get.map(_.perCountry.get(country))
}

object IORefService {
  def apply[F[_]: Sync](storeRef: Ref[F, SummaryData]): DatabaseService[F] =
    new IORefService(storeRef)
}