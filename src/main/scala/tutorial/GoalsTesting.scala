package tutorial

import cells.Cell
import cells.Cell.VectorToJS
import facade.googleanalyticsapi.goals.Goals
import facade.spreadsheetapp.SpreadsheetApp

import scala.scalajs.js.annotation.JSExportTopLevel

object GoalsTesting {


  /**
   * Finds all Google Analytics goals of all of your accounts and list them in the active sheet.
   *
   * If you have many accounts, and many goals, this may fail because the request is too big. If that's the case, you
   * can change the arguments in the Goals.list method.
   *
   * Access to Google Analytics from the spreadsheets has to be enabled. See, e.g.,
   * https://developers.google.com/analytics/solutions/articles/reporting-apps-script
   */
  @JSExportTopLevel("findListOfGoals")
  def findListOfGoals(): Unit = {
    val listResponse = Goals.list("~all", "~all", "~all")

    val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

    val goalsWithUrl = listResponse.items.filter(_.`type` == "URL_DESTINATION").toVector.map(goal => {
      Vector(
        Cell(goal.id),
        Cell(goal.name),
        Cell(goal.urlDestinationDetails.url),
        Cell(goal.accountId),
        Cell(goal.profileId),
        Cell(goal.`type`),
        Cell(goal.active),
        Cell(goal.created),
        Cell(goal.kind)
      )
    })

    if (goalsWithUrl.nonEmpty) {
      val rangeNotation = s"A1:${alphabet(goalsWithUrl.head.length - 1)}${goalsWithUrl.length}"

      val range = SpreadsheetApp.getActiveSpreadsheet().getActiveSheet().getRange(rangeNotation)

      range.setValues(goalsWithUrl.toGoogleCells)
    }
  }

}
