package facade.xmlservice

import scala.scalajs.js


@js.native
trait Element extends js.Object {

  def getChild(name: String = null, namespace: Namespace = null): Element = js.native

  def getChildren(name: String = null, namespace: Namespace = null): js.Array[Element] = js.native

  def getText(): String = js.native

  def getValue(): String = js.native

}
