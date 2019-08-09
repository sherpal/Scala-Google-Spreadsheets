package cells.customfunctions

import cells.Cell
import cells.Cell._

import scala.scalajs.js
import scala.util.Try

/**
 * An [[Encoder]] receives inputs from the spreadsheet cells and translates it to an instance of T.
 *
 * The user must implement the method encode that returns a [[Try]] of T, containing either the
 * transformed input, or an exception describing what went wrong when parsing the input.
 *
 * @tparam T output type of this encoder.
 */
trait Encoder[+T] {

  def encode(data: js.Array[js.Array[Data]]): Try[T]

  final def apply(input: Input): Try[T] = input match {
    case input: js.Array[_] => encode(input.asInstanceOf[js.Array[js.Array[Data]]])
    case input => encode(js.Array(js.Array(input.asInstanceOf[Data])))
  }

}

object Encoder {

  implicit final val cellsEncoder: Encoder[Vector[Vector[Cell]]] =
    (data: Output) => Try(data.asScala)

  implicit final val vectorStringEncoder: Encoder[Vector[Vector[String]]] =
    (data: Output) => Try(data.asScala.deepMap(_.toString))

  implicit final val vectorIntEncoder: Encoder[Vector[Vector[Int]]] =
    (data: Output) => Try(data.asScala.deepMap(_.toInt.get))

  implicit final val vectorTryIntEncoder: Encoder[Vector[Vector[Try[Int]]]] =
    (data: Output) => Try(data.asScala.deepMap(_.toInt))

}
