package cells.customfunctions.customfunctionsimpl

import cells.customfunctions.{Decoder, Input, Output}

import scala.scalajs.js
import scala.util.{Failure, Success, Try}

final class CustomFunction0[+U](f: () => U)(implicit decoder: Decoder[U]) {

  def apply(input: Input): Output = {
    Try(decoder(f())) match {
      case Success(value) => value
      case Failure(e) => js.Array(js.Array(e.getMessage))
    }
  }

}

object CustomFunction0 {

  def apply[U](f: () => U)(implicit decoder: Decoder[U]): CustomFunction0[U] =
    new CustomFunction0(f)

  implicit final class FromFunction0[+U](f: () => U)(implicit decoder: Decoder[U]) {
    def asCustomFunction: CustomFunction0[U] = CustomFunction0(f)
  }

}
