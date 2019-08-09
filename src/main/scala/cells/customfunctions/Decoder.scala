package cells.customfunctions

import cells.Cell.Data

import scala.scalajs.js

trait Decoder[-U] {

  def decodeU(u: U): js.Array[js.Array[Data]]

  final def apply(u: U): js.Array[js.Array[Data]] = decodeU(u)

}

object Decoder {

  implicit final val stringDecoder: Decoder[String] = (u: String) => js.Array(js.Array(u))
  implicit final val doubleDecoder: Decoder[Double] = (u: Double) => js.Array(js.Array(u))
  implicit final val intDecoder: Decoder[Int] = (u: Int) => js.Array(js.Array(u))
  implicit final val dateDecoder: Decoder[js.Date] = (u: js.Date) => js.Array(js.Array(u))
  implicit final val booleanDecoder: Decoder[Boolean] = (u: Boolean) => js.Array(js.Array(u))

}