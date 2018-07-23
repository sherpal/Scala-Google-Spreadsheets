package facade.spreadsheetapp

import facade.charts.{Chart, EmbeddedChart}
import facade.charts.chartbuilder.EmbeddedChartBuilder

import scala.scalajs.js

/**
 * https://developers.google.com/apps-script/reference/spreadsheet/sheet
 */
@js.native
trait Sheet extends js.Object {

  def getName(): String = js.native

  def getRange(a1Notation: String): Range = js.native

  def getRange(row: Int, column: Int, numRows: Int, numColumns: Int): Range = js.native

  def insertChart(chart: EmbeddedChart): Unit = js.native

  def newChart(): EmbeddedChartBuilder = js.native

}
