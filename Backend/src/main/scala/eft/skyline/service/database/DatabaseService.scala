package eft.skyline.service.database

import eft.skyline.domain.Data.{CovidData, SummaryData}
/*
* @typeparam: F[_]: Abstraction for an Effect type that may or may not do I/O
* */
trait DatabaseService[F[_]] {

  def setCovidData(data: SummaryData): F[Unit]
  def getGlobalCovidData(): F[CovidData]
  def getCountryCovidData(country: String): F[Option[CovidData]]

}
