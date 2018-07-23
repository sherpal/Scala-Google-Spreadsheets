package facade.spreadsheetapp

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal


/**
 * https://developers.google.com/apps-script/reference/spreadsheet/spreadsheet
 */
@js.native
@JSGlobal("SpreadsheetApp")
object SpreadsheetApp extends js.Object {

  def getActiveSpreadsheet(): SpreadSheet = js.native

  def getActiveSheet(): Sheet = js.native

}
