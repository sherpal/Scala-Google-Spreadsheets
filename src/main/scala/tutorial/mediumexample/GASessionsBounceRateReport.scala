package tutorial.mediumexample

import GASessionsBounceRateReport.DataRow
import cells.Cell.Data
import cells.customfunctions.{Decoder, Output}

import scala.scalajs.js

/**
 * Contains the [[DataRow]] information for different kinds of information type. For example, "Current Month" or
 * "Year to Date".
 * @param info map from the information type to the information contents.
 */
final case class GASessionsBounceRateReport(info: Map[String, DataRow]) {
  def row(infoType: String): js.Array[Data] = {
    val theseInfo = info(infoType)
    js.Array(
      infoType,
      theseInfo.sessionsInfo.desktop, theseInfo.sessionsInfo.tablet, theseInfo.sessionsInfo.mobile,
      theseInfo.sessionsInfo.total,
      theseInfo.bounceRateInto.desktop, theseInfo.bounceRateInto.tablet, theseInfo.bounceRateInto.mobile,
      theseInfo.bounceRateInto.global
    )
  }
}

object GASessionsBounceRateReport {

  final case class SessionsInfo(mobile: Int, desktop: Int, tablet: Int) {
    def total: Int = mobile + desktop + tablet
  }

  /** We include the global value here since it has to be a weighted average and the info aren't there. */
  final case class BounceRateInfo(mobile: Double, desktop: Double, tablet: Double, global: Double)

  final case class DataRow(sessionsInfo: SessionsInfo, bounceRateInto: BounceRateInfo)

  implicit final val gaReportDecoder: Decoder[GASessionsBounceRateReport] = (u: GASessionsBounceRateReport) => js.Array(
    js.Array("", "Sessions", "", "", "", "Bounce Rate"),
    js.Array("", "Desktop", "Tablet", "Mobile", "Total", "Desktop", "Tablet", "Mobile", "Global"),
    u.row("Current Month"),
    u.row("Previous Month"),
    u.row("Year to Date")
  )

}
