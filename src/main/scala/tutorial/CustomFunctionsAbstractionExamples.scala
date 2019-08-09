package tutorial

import cells.Cell
import cells.Cell._
import cells.customfunctions.{Encoder, Input, Output}
import cells.customfunctions.customfunctionsimpl.CustomFunction1.FromFunction1
import cells.customfunctions.customfunctionsimpl.CustomFunction2.FromFunction2

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportTopLevel
import scala.util.Try

object CustomFunctionsAbstractionExamples {

  final case class Foo(bar: String, babar: Int)

  implicit val fooEncoder: Encoder[Vector[Foo]] =
    (data: js.Array[js.Array[Data]]) => data.asScala.map(v => Foo(v(0).toString, v(1).toInt.get))

  /** Counts the number of [[Foo]] for which `babar` is bigger than 10. */
  def countBigFoo(foos: Vector[Foo]): Int = foos.count(_.babar > 10)

  /**
   * Export the custom function for google sheets.
   * The encoder for vectors of [[Foo]] is defined above, and the decoder for Int is defined in the
   * companion object of the [[cells.customfunctions.Decoder]] trait.
   *
   * The `asCustomFunction` implicit method comes from [[FromFunction1]] imported above.
   */
  @JSExportTopLevel("COUNTBIGFOO")
  def jsCountBigFoo(input: Input): Output = (countBigFoo _).asCustomFunction(input)



  def sumByCategory(categories: Vector[Vector[String]], values: Vector[Vector[Try[Int]]]): Vector[Vector[Cell]] = {
    categories.flatten.zip(values.flatten.map(_.getOrElse(0)))
      .filterNot(_._1.isEmpty)
      .groupBy(_._1)
      .view.mapValues(_.map(_._2).sum)
      .toVector.map { case (cat, value) => Vector(Cell(cat), Cell(value)) }
  }

  @JSExportTopLevel("SUMBYCATEGORY")
  def jsSumByCategories(categories: Input, values: Input): Output =
    (sumByCategory _).asCustomFunction(categories, values)


}
