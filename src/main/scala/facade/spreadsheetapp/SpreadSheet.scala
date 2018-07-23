package facade.spreadsheetapp

import scala.scalajs.js

/**
 * https://developers.google.com/apps-script/reference/spreadsheet/spreadsheet
 */
@js.native
trait SpreadSheet extends js.Object {

  def getActiveSheet(): Sheet = js.native

  def getId(): Int = js.native

  def getRangeList(a1Notations: js.Array[String]): RangeList = js.native

  def getSheetByName(name: String): Sheet = js.native

  def insertSheet(): Sheet = js.native

  def insertSheet(sheetName: String): Sheet = js.native

}
