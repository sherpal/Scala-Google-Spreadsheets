package cells.customfunctions

import cells.Cell
import cells.Cell.Data
import cells.Cell.VectorToJS

import scala.scalajs.js

/**
 * A [[Decoder]] takes the result of a custom function, of type U, and decodes it as an [[Output]] in
 * order to be sent back to the Google sheets.
 *
 * @tparam U type to decode as an [[Output]]
 */
trait Decoder[-U] {

  def decodeU(u: U): Output

  final def apply(u: U): Output = decodeU(u)

}

object Decoder {

  implicit final val identityDecoder: Decoder[Output] =
    (u: js.Array[js.Array[Data]]) => u

  implicit final val cellDecoder: Decoder[Vector[Vector[Cell]]] =
    (u: Vector[Vector[Cell]]) => u.toGoogleCells

  implicit final val stringDecoder: Decoder[String] = (u: String) => js.Array(js.Array(u))
  implicit final val doubleDecoder: Decoder[Double] = (u: Double) => js.Array(js.Array(u))
  implicit final val intDecoder: Decoder[Int] = (u: Int) => js.Array(js.Array(u))
  implicit final val dateDecoder: Decoder[js.Date] = (u: js.Date) => js.Array(js.Array(u))
  implicit final val booleanDecoder: Decoder[Boolean] = (u: Boolean) => js.Array(js.Array(u))

}