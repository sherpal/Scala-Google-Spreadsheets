package cells.customfunctions

import cells.Cell._

import scala.scalajs.js
import scala.util.Try

trait Encoder[+T] {

  def encode(data: js.Array[js.Array[Data]]): T

  final def apply(input: Input): T = input match {
    case input: js.Array[_] => encode(input.asInstanceOf[js.Array[js.Array[Data]]])
    case input => encode(js.Array(js.Array(input.asInstanceOf[Data])))
  }

}

object Encoder {

  implicit final val vectorStringEncoder: Encoder[Vector[Vector[String]]] =
    (data: js.Array[js.Array[Data]]) => data.asScala.deepMap(_.toString)

  implicit final val vectorIntEncoder: Encoder[Vector[Vector[Try[Int]]]] =
    (data: js.Array[js.Array[Data]]) => data.asScala.deepMap(_.toInt)

}
