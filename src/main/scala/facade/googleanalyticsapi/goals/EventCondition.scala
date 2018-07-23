package facade.googleanalyticsapi.goals

import scala.scalajs.js


@js.native
trait EventCondition extends js.Object {

  /**
   * Type of comparison. Possible values are LESS_THAN, GREATER_THAN or EQUAL.
   */
  val comparisonType: String = js.native

  /**
   * Value used for this comparison.
   */
  val comparisonValue: Double = js.native

  /**
   * Expression used for this match.
   */
  val expression: String = js.native

  /**
   * Type of the match to be performed. Possible values are REGEXP, BEGINS_WITH, or EXACT.
   */
  val matchType: String = js.native

  /**
   * Type of this event condition. Possible values are CATEGORY, ACTION, LABEL, or VALUE.
   */
  val `type`: String = js.native

}
