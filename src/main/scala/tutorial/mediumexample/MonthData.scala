package tutorial.mediumexample

import cells.Cell._
import cells.customfunctions.Encoder

import scala.scalajs.js
import scala.util.Try

final case class MonthData(date: js.Date, device: String, sessions: Int, bounceRate: Double)

object MonthData {

  /** Encoder for Vector of [[MonthData]]. */
  implicit final val monthDataVectorEncoder: Encoder[Vector[MonthData]] = (data: js.Array[js.Array[Data]]) => Try(
    data.asScala
      .filter(_.forall(_.nonEmpty)) // keeping only non empty rows
      .map(row => MonthData(row(0).toDate.get, row(1).toString, row(2).toInt.get, row(3).toDouble.get))
  )

}
