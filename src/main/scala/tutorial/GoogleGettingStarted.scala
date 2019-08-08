package tutorial

import cells.Cell.Data
import facade.urlfetchservice.UrlFetchApp
import facade.xmlservice.XmlService

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportTopLevel


/**
 * We show how to adapt the Google getting started functions in here.
 *
 * See url https://developers.google.com/apps-script/guides/sheets/functions
 *
 *
 * The Data type is an alias for String | Double | Boolean | js.Date, which are the possible returned types in cells.
 *
 *
 * The DOUBLE function in the page is designed for both js.Array[js.Array[Data] ] and Data. In our case, we need two
 * overloaded methods.
 */
object GoogleGettingStarted {

  @JSExportTopLevel("DOUBLE")
  def double(x: Double): Double = 2 * x

  /**
   * Multiplies the input value by 2.
   *
   * @param xs input The value or range of cells to multiply.
   * @return The input multiplied by 2.
   */
  @JSExportTopLevel("DOUBLE")
  def double(xs: js.Array[js.Array[Data]]): js.Array[js.Array[Data]] =
    xs.map(_.map(_.asInstanceOf[Double] * 2).map(_.asInstanceOf[Data]))



  /**
   * Show the title and date for the first page of posts on the G Suite
   * Developer blog.
   *
   * @return Two columns of data representing posts on the G Suite
   *     Developer blog.
   */
  @JSExportTopLevel("GETBLOGPOSTS")
  def getBlogPosts(): js.Array[js.Array[String]] = {

    val url = "https://gsuite-developers.googleblog.com/atom.xml"
    val xml = UrlFetchApp.fetch(url).getContentText()
    val document = XmlService.parse(xml)
    val root = document.getRootElement()
    val atom = XmlService.getNamespace("http://www.w3.org/2005/Atom")

    val entries = root.getChildren("entry", atom)

    entries.map(element =>
      js.Array(element.getChild("title", atom).getText(), element.getChild("published", atom).getValue())
    )

  }

}
