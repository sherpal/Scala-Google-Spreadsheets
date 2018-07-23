package facade.charts.chartbuilder

import facade.charts.{Chart, DataTableBuilder, EmbeddedChart}
import facade.spreadsheetapp.Range

import scala.scalajs.js

/**
 * https://developers.google.com/apps-script/reference/spreadsheet/embedded-chart-builder
 */
@js.native
trait EmbeddedChartBuilder extends js.Object {

  def addRange(range: Range): this.type = js.native

  def asPieChart(): EmbeddedPieChartBuilder = js.native

  def build(): EmbeddedChart = js.native

  def setDataTable(dataTable: DataTableBuilder): this.type = js.native

  def setOption(option: String, value: js.Any): this.type = js.native

  def setPosition(anchorRowPos: Int, anchorColPos: Int, offsetX: Int, offsetY: Int): this.type = js.native

  def setTitle(title: String): this.type = js.native

}
