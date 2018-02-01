package facade.xmlservice

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal

@js.native
@JSGlobal("XmlService")
object XmlService extends js.Object {

  def getNamespace(prefix: String = null): Namespace = js.native

  def parse(xml: String): Document = js.native

}
