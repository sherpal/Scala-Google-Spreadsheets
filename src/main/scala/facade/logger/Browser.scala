package facade.logger

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal


@js.native
@JSGlobal("Browser")
object Browser extends js.Object {

  def msgBox(message: String): Unit = js.native

}
