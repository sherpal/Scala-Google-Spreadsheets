package tutorial.mediumexample

import cells.customfunctions.{Input, Output}
import cells.customfunctions.customfunctionsimpl.CustomFunction2.FromFunction2
import tutorial.mediumexample.GASessionsBounceRateReport.{BounceRateInfo, DataRow, SessionsInfo}

import scala.scalajs.js
import scala.scalajs.js.Date
import scala.scalajs.js.annotation.JSExportTopLevel

object MediumExample {

  implicit class DateOrdered(date: js.Date) extends Ordered[js.Date] {
    def compare(that: Date): Int = {
      if (date.getFullYear != that.getFullYear) date.getFullYear - that.getFullYear
      else if (date.getMonth != that.getMonth) date.getMonth - that.getMonth
      else date.getDay - that.getDay
    }.toInt
  }

  def gaSessionsBounceRateReport(data: Vector[MonthData], currentMonth: js.Date): GASessionsBounceRateReport = {
    def betweenDates(from: js.Date, to: js.Date, d: Vector[MonthData]): Vector[MonthData] =
      d.filter(_.date >= from).filter(_.date <= to)

    val firstDayOfYear = new js.Date(currentMonth.getFullYear.toInt, 0)
    val previousMonth = new js.Date(currentMonth.getFullYear.toInt, currentMonth.getMonth.toInt - 1)

    val ytdData = betweenDates(firstDayOfYear, currentMonth, data)
    val currentMonthData = betweenDates(currentMonth, currentMonth, data)
    val previousMonthData = betweenDates(previousMonth, previousMonth, data)

    def sessionInfo(d: Vector[MonthData]): SessionsInfo = {
      val agg = d.groupMapReduce(_.device.toLowerCase)(_.sessions)(_ + _)
      SessionsInfo(agg.getOrElse("mobile", 0), agg.getOrElse("desktop", 0), agg.getOrElse("tablet", 0))
    }

    def bounceRateInfo(d: Vector[MonthData], sessionsInfo: SessionsInfo): BounceRateInfo = {
      val agg = d.groupMapReduce(_.device.toLowerCase)(r => r.sessions * r.bounceRate)(_ + _)
      BounceRateInfo(
        agg.getOrElse("mobile", 0.0) / sessionsInfo.mobile,
        agg.getOrElse("desktop", 0.0) / sessionsInfo.desktop,
        agg.getOrElse("tablet", 0.0) / sessionsInfo.tablet,
        agg.values.sum / sessionsInfo.total
      )
    }

    val currentMonthSessions = sessionInfo(currentMonthData)
    val currentMonthBounceRates = bounceRateInfo(currentMonthData, currentMonthSessions)

    val previousMonthSessions = sessionInfo(previousMonthData)
    val previousMonthBounceRates = bounceRateInfo(previousMonthData, previousMonthSessions)

    val ytdSessions = sessionInfo(ytdData)
    val ytdBounceRates = bounceRateInfo(ytdData, ytdSessions)

    GASessionsBounceRateReport(Map(
      "Current Month" -> DataRow(currentMonthSessions, currentMonthBounceRates),
      "Previous Month" -> DataRow(previousMonthSessions, previousMonthBounceRates),
      "Year to Date" -> DataRow(ytdSessions, ytdBounceRates)
    ))
  }

  /**
   * Computes the Current Month, Year to Date and Previous Month values for the given data.
   * @param data matrix with four columns: date, device, sessions and bounce rate
   * @param currentMonth the first day of the current month
   * @return
   */
    @JSExportTopLevel("GASESSIONSBOUNCERATEREPORT")
  def jsGaSessionsBounceRateReport(data: Input, currentMonth: Input): Output =
    (gaSessionsBounceRateReport _).asCustomFunction(data, currentMonth)

}
