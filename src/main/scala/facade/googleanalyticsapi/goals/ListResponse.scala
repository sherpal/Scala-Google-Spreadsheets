package facade.googleanalyticsapi.goals

import scala.scalajs.js

@js.native
trait ListResponse extends js.Object {

  val kind: String = js.native

  val username: String = js.native

  val totalResults: Int = js.native

  val startIndex: Int = js.native

  val itemsPerPage: Int = js.native

  val previousLink: String = js.native

  val nextLink: String = js.native

  val items: js.Array[Goals] = js.native



}
