package facade.spreadsheetapp

import cells.Cell.Data

import scala.scalajs.js

@js.native
trait Range extends js.Object {

  def getCell(row: Int, column: Int): Range = js.native

  def getValue(): Data = js.native

  def getValues(): js.Array[js.Array[Data]] = js.native

}
