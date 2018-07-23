package facade.googleanalyticsapi.goals

import scala.scalajs.js


@js.native
trait EventDetails extends js.Object {

  val eventConditions: js.Array[EventCondition] = js.native

  /**
   * Determines if the event value should be used as the value for this goal.
   */
  val useEventValue: Boolean = js.native

}
