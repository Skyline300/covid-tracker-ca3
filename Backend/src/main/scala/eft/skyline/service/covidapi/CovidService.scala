package eft.skyline.service.covidapi

import eft.skyline.domain.Data.SummaryData
/*
* @typeparam: F[_]: Abstraction for an Effect type that may or may not do I/O
* */
trait CovidService[F[_]] {

  def requestGlobalData(): F[SummaryData]

}
