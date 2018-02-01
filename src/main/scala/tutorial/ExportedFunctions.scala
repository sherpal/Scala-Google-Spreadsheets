package tutorial

import cells.Cell.Data

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportTopLevel

/**
 * This object contains examples of functions that are exported to Google custom functions.
 */
object ExportedFunctions {

  /**
   * Returns the average of the cells. Empty cells are treated as 0. Non double cells are removed from collection.
   *
   * @param xs {number} the cells to take the mean of
   * @return {number} the mean
   */
  @JSExportTopLevel("exported.MEAN")
  def mean(xs: js.Array[js.Array[Data]]): Double = {
    val flat = xs.flatten.map((_: Any) match {
      case s: String if s == "" => 0.0
      case elem => elem
    }).filter((_: Any).isInstanceOf[Double]).map(_.asInstanceOf[Double])
    flat.sum / flat.length
  }

  @JSExportTopLevel("exported.MEAN")
  def mean(x: Double): Double = x

  /**
   * Returns the number of seconds in the form mm:ss.
   *
   * @param seconds {number} the number of seconds
   */
  @JSExportTopLevel("exported.SECONDSTOMINUTESECONDS")
  def seconds2MinuteSeconds(seconds: Double): String = {
    val roundedSeconds = math.round(seconds)
    val s = roundedSeconds % 60
    s"${roundedSeconds / 60}:${if (s < 10) s"0$s" else s}"
  }

}
