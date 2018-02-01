package facade.urlfetchservice

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal


@js.native
@JSGlobal("UrlFetchApp")
object UrlFetchApp extends js.Object {

  def fetch(url: String): HTTPResponse = js.native

}
