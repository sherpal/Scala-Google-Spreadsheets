package facade.spreadsheetapp

import scala.scalajs.js

@js.native
trait Sheet extends js.Object {

  def getName(): String = js.native

  def getRange(a1Notation: String): Range = js.native

}
