package cells.customfunctions.customfunctionsimpl

import cells.Cell.Data
import cells.customfunctions.{Decoder, Encoder, Input}

import scala.scalajs.js

final class CustomFunction1[-T, +U](f: T => U)(implicit encoder: Encoder[T], decoder: Decoder[U]) {

  def apply(input: Input): js.Array[js.Array[Data]] = decoder(f(encoder(input)))

}

object CustomFunction1 {

  def apply[T, U](f: T => U)(implicit encoder: Encoder[T], decoder: Decoder[U]): CustomFunction1[T, U] =
    new CustomFunction1(f)

  implicit final class FromFunction1[-T, +U](f: T => U)(implicit encoder: Encoder[T], decoder: Decoder[U]) {
    def asCustomFunction: CustomFunction1[T, U] = CustomFunction1(f)
  }

}
