package facade.googleanalyticsapi.goals

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal

/**
 * https://developers.google.com/analytics/devguides/config/mgmt/v3/mgmtReference/management/goals
 */
@js.native
trait Goals extends js.Object {
  val accountId: String = js.native

  val active: Boolean = js.native

  val created: js.Date = js.native

  val eventDetails: EventDetails = js.native

  val id: String = js.native

  val kind: String = js.native

  val name: String = js.native

  val profileId: String = js.native

  /**
   * Goal type. Possible values are URL_DESTINATION, VISIT_TIME_ON_SITE, VISIT_NUM_PAGES, and EVENT.
   */
  val `type`: String = js.native

  val urlDestinationDetails: UrlDestinationDetails = js.native

}

@js.native
@JSGlobal("Analytics.Management.Goals")
object Goals extends js.Object {

  /**
   * https://developers.google.com/analytics/devguides/config/mgmt/v3/mgmtReference/management/goals/list
   */
  def list(accountId: String, webPropertyId: String, profileId: String): ListResponse = js.native

}
