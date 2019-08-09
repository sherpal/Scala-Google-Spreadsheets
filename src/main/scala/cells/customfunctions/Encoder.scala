package cells.customfunctions

import cells.Cell.Data

import scala.scalajs.js

trait Encoder[+T] {

  def encode(data: js.Array[js.Array[Data]]): T

  final def apply(input: Input): T = input match {
    case input: js.Array[_] => encode(input.asInstanceOf[js.Array[js.Array[Data]]])
    case input => encode(js.Array(js.Array(input.asInstanceOf[Data])))
  }

}
