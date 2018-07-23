package facade.spreadsheetapp

import scala.scalajs.js

/**
 * https://developers.google.com/apps-script/reference/spreadsheet/range-list
 */
@js.native
trait RangeList extends js.Object {

  def getRanges(): js.Array[Range] = js.native

}
