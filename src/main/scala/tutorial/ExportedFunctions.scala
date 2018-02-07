package tutorial

import cells.Cell
import cells.Cell.Data
import cells.Cell.VectorToJS
import exceptions.WrongDataTypeException

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportTopLevel

/**
 * This object contains examples of functions that are exported to Google custom functions.
 */
object ExportedFunctions {

  /**
   * Returns the average of the cells. Empty cells are treated as 0. Non double cells are removed from collection.
   *
   * @param xs {number} the cells to take the mean of
   * @return {number} the mean
   */
  @JSExportTopLevel("exported.MEAN")
  def mean(xs: js.Array[js.Array[Data]]): Double = {
    val flat = xs.flatten.map((_: Any) match {
      case s: String if s == "" => 0.0
      case elem => elem
    }).filter((_: Any).isInstanceOf[Double]).map(_.asInstanceOf[Double])
    flat.sum / flat.length
  }

  @JSExportTopLevel("exported.MEAN")
  def mean(x: Double): Double = x

  /**
   * Returns the number of seconds in the form mm:ss.
   *
   * @param seconds {number} the number of seconds
   */
  @JSExportTopLevel("exported.SECONDSTOMINUTESECONDS")
  def seconds2MinuteSeconds(seconds: Double): String = {
    val roundedSeconds = math.round(seconds)
    val s = roundedSeconds % 60
    s"${roundedSeconds / 60}:${if (s < 10) s"0$s" else s}"
  }


  /**
   * An example of how to implement the sum function in Scala.
   *
   * We consider only elements that are Double by filtering on the isNumeric method.
   */
  @JSExportTopLevel("exported.CUSTOMSUM")
  def sum(elems: js.Array[js.Array[Data]]): Double = {
    Cell.fromJSArray(elems).flatten.filter(_.isNumeric).map(_.toDouble).sum
  }


  /**
   * Below is an example of actually using Scala code for treating data in the spreadsheet.
   *
   * Remember that computations are made on the server side, and calling lots of custom functions can be slow.
   * The best way around this is to create functions that take a full table of data, treat them, and return a table with
   * the results.
   *
   * In this example, we'll assume that we have a table with people information, where each row contains the information
   * relative to one person, and each row contain the following information, in that order:
   * - first name (String)
   * - last name (String)
   * - date of birth (js.Date)
   * - sex (String, M for male and F for female)
   * - monthly income (say, in euros) (Double)
   *
   * In Scala, we'll represent the data using case classes.
   */

  private sealed trait Sex
  private case object Male extends Sex
  private case object Female extends Sex

  private case class Person(firstName: String, lastName: String, dateOfBirth: js.Date, sex: Sex, income: Double) {

    def age: Int = {
      val now = new js.Date(js.Date.now())

      val hadBirthday: Boolean = now.getMonth > dateOfBirth.getMonth ||
        (now.getMonth == dateOfBirth.getMonth && now.getDay >= dateOfBirth.getDay)

      now.getFullYear() - dateOfBirth.getFullYear() + (if (hadBirthday) 0 else -1)
    }

    def toRow: Vector[Cell] = Vector(
      Cell(firstName), Cell(lastName), Cell(dateOfBirth), Cell(if (sex == Male) "M" else "F"), Cell(income)
    )

  }

  private def rowToPerson(row: Vector[Cell]): Person = Person(
    row(0).value.asInstanceOf[String], row(1).value.asInstanceOf[String],
    row(2).toDate, if (row(3).value.asInstanceOf[String] == "M") Male else Female,
    row(4).toDouble
  )


  /**
   * Returns a table containing only the adults in the data.
   * An adult is a person whose age is at least 18.
   */
  @JSExportTopLevel("exported.GETADULTS")
  def adults(data: js.Array[js.Array[Data]]): js.Array[js.Array[Data]] =
    Cell.fromJSArray(data).map(rowToPerson).filter(_.age >= 18).map(_.toRow).toGoogleCells

  /**
   * Returns a table containing the adults whose income is above the average of income.
   * The last row also contains the average income, for reference.
   */
  @JSExportTopLevel("exported.ABOVEINCOMEAVERAGE")
  def aboveIncomeAverage(data: js.Array[js.Array[Data]]): js.Array[js.Array[Data]] = {
    val persons = Cell.fromJSArray(data).map(rowToPerson)
    val adults = persons.filter(_.age >= 18)

    val incomeAverage = adults.map(_.income).sum / adults.length

    val adultsAboveAverage = adults.filter(_.income >= incomeAverage)

    (adultsAboveAverage.map(_.toRow) :+ Vector(Cell(incomeAverage))).toGoogleCells
  }

  private def scalarProd(x: Vector[Double], y: Vector[Double]): Double = x.zip(y).map(elem => elem._1 * elem._2).sum

  private def cost(
                    theta: Vector[Double],
                    zippedTraining: Vector[(Vector[Double], Double)]
                  ): Double =
    zippedTraining.map({ case (x, y) => math.pow(scalarProd(theta, x) - y, 2) }).sum / zippedTraining.length

  private def linearRegression(
                        trainingDataX: Vector[Vector[Double]],
                        trainingDataY: Vector[Double]
                      ): Vector[Double] = {
    def trainingSetSize: Int = trainingDataY.length
    def trainingDimension: Int = trainingDataX.head.length

    def zippedTraining = trainingDataX.map(1.0 +: _).zip(trainingDataY)

    def grad(theta: Vector[Double], alpha: Double): Vector[Double] =
      (0 to trainingDimension).map(j => zippedTraining.map({
        case (x, y) => (scalarProd(theta, x) - y) * x(j)
      }).sum / trainingSetSize * alpha).toVector

    val tol: Double = 0.000001
    val maxIteration: Int = 200

    def makeIterations(prevTheta: Vector[Double], prevCost: Double, alpha: Double, iteration: Int): Vector[Double] = {
      if (iteration > maxIteration) prevTheta
      else {
        val theta = prevTheta.zip(grad(prevTheta, alpha)).map(elem => elem._1 - elem._2)
        val newCost = cost(theta, zippedTraining)

        if (newCost > prevCost) makeIterations(prevTheta, prevCost, alpha / 2, iteration + 1)
        else if (prevCost - newCost < tol) theta
        else makeIterations(theta, newCost, alpha, iteration + 1)
      }
    }

    val startingTheta = Vector[Double]().padTo(trainingDimension + 1, 0.0)

    makeIterations(startingTheta, cost(startingTheta, zippedTraining), 0.1, 0)
  }


  /**
   * Computes the hypothesis function corresponding to the data. Similar to LINEST built in function, but works on
   * multi-dimensional data. Quite slow though...
   *
   * Each row of the X matrix must be one data.
   * Each row of the Y matrix must be an array of one element corresponding to the X data.
   */
  @JSExportTopLevel("exported.LINEARREGRESSION")
  def linearRegression(
                        trainingDataX: js.Array[js.Array[Data]],
                        trainingDataY: js.Array[js.Array[Data]]
                      ): js.Array[js.Array[Data]] =
    try {
      val startTime = js.Date.now
      val result = Vector(linearRegression(
        Cell.fromJSArray(trainingDataX).map(_.map(_.toDouble)),
        Cell.fromJSArray(trainingDataY).map(_.head.toDouble)
      ).map(new Cell(_)))

      (result :+ Vector(Cell("Time taken"), Cell(js.Date.now - startTime))).toGoogleCells
    } catch {
      case e: WrongDataTypeException => js.Array(js.Array(e.msg))
      case e: Throwable => throw e
    }


  /**
   * Returns the predicted income at a given age, given the income of the people in the data set.
   *
   * This function performs a linear regression on the data set, and return the predicted value from that linear
   * regression. This is probably a bad model, since there is no reason why income should be proportional to the age,
   * and not quadratic of sub-linear.
   */
  @JSExportTopLevel("exported.PREDICTINCOMEATAGE")
  def predictIncomeAtAge(data: js.Array[js.Array[Data]], age: Int): Double = {
    val persons = Cell.fromJSArray(data).map(rowToPerson)
    val adults = persons.filter(_.age >= 18)

    val theta = linearRegression(adults.map(p => Vector(p.age.toDouble)), adults.map(_.income))

    theta(0) + theta(1) * age
  }


}
