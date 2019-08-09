package cells.customfunctions.customfunctionsimpl

import cells.customfunctions.{Decoder, Encoder, Input, Output}

import scala.scalajs.js
import scala.util.{Failure, Success}

final class CustomFunction1[-T, +U](f: T => U)
                                   (implicit encoder: Encoder[T], decoder: Decoder[U])
  extends (Input => Output) {

  def apply(input: Input): Output = {
    (for (arg <- encoder(input)) yield decoder(f(arg))) match {
      case Success(value) => value
      case Failure(e) => js.Array(js.Array(e.getMessage))
    }
  }

}

object CustomFunction1 {

  def apply[T, U](f: T => U)(implicit encoder: Encoder[T], decoder: Decoder[U]): CustomFunction1[T, U] =
    new CustomFunction1(f)

  implicit final class FromFunction1[-T, +U](f: T => U)(implicit encoder: Encoder[T], decoder: Decoder[U]) {
    def asCustomFunction: CustomFunction1[T, U] = CustomFunction1(f)
  }

}
