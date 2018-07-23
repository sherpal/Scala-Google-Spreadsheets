package facade.spreadsheetapp

import cells.Cell.Data

import scala.scalajs.js

/**
 * https://developers.google.com/apps-script/reference/spreadsheet/range
 */
@js.native
trait Range extends js.Object {

  def getCell(row: Int, column: Int): Range = js.native

  def getValue(): Data = js.native

  def getValues(): js.Array[js.Array[Data]] = js.native

  def setValues(values: js.Array[js.Array[Data]]): Unit = js.native

}
