package facade.spreadsheetapp

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal


@js.native
@JSGlobal("SpreadsheetApp")
object SpreadsheetApp extends js.Object {

  def getActiveSpreadsheet(): SpreadSheet = js.native

  def getActiveSheet(): Sheet = js.native

}
