package cells.customfunctions.customfunctionsimpl

import cells.Cell.Data
import cells.customfunctions.{Decoder, Encoder, Input}

import scala.scalajs.js

final class CustomFunction2[-T1, -T2, +U](f: (T1, T2) => U)
                                         (implicit
                                          encoder1: Encoder[T1],
                                          encoder2: Encoder[T2],
                                          decoder: Decoder[U]) {

  def apply(input1: Input, input2: Input): js.Array[js.Array[Data]] = {
    val arg1 = encoder1(input1)
    val arg2 = encoder2(input2)
    decoder(f(arg1, arg2))
  }

}

object CustomFunction2 {

  def apply[T1, T2, U](f: (T1, T2) => U)
                      (implicit encoder1: Encoder[T1], encoder2: Encoder[T2],
                       decoder: Decoder[U]): CustomFunction2[T1, T2, U] =
    new CustomFunction2(f)

  implicit final class FromFunction2[-T1, -T2, +U](f: (T1, T2) => U)
                                                  (implicit encoder1: Encoder[T1], encoder2: Encoder[T2],
                                                   decoder: Decoder[U]) {
    def asCustomFunction: CustomFunction2[T1, T2, U] = CustomFunction2(f)
  }

}
