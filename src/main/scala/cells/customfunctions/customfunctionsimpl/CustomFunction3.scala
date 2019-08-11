package cells.customfunctions.customfunctionsimpl
import cells.customfunctions.{Decoder, Encoder, Input, Output}
import scala.scalajs.js
import scala.util.{Failure, Success}

/**
 * A [[CustomFunction3]] represents a Google custom function taking 3 inputs and returning an [[Output]].
 *
 * @param f function to apply to the transformed arguments
 * @param encoder1 encoder to go from Input type to type T1
 * @param encoder2 encoder to go from Input type to type T2
 * @param encoder3 encoder to go from Input type to type T3
 * @param decoder decoder to go from type U to Output type
 * @tparam U return type
 */
final class CustomFunction3
[-T1, -T2, -T3, +U]
(f: (T1, T2, T3) => U)
(implicit
 encoder1: Encoder[T1],
 encoder2: Encoder[T2],
 encoder3: Encoder[T3],
 decoder: Decoder[U])
  extends ((Input, Input, Input) => Output) {

  def apply(input1: Input, input2: Input, input3: Input): Output = {
    (
      for {
        arg1 <- encoder1(input1)
        arg2 <- encoder2(input2)
        arg3 <- encoder3(input3)
        output = f(arg1, arg2, arg3)
      } yield decoder(output)
      ) match {
      case Success(value) => value
      case Failure(exception) => js.Array(js.Array(exception.getMessage))
    }
  }

}

object CustomFunction3 {

  def apply[T1, T2, T3, U]
  (f: (T1, T2, T3) => U)
  (implicit
   encoder1: Encoder[T1],
   encoder2: Encoder[T2],
   encoder3: Encoder[T3],
   decoder: Decoder[U]): CustomFunction3[T1, T2, T3, U] =
    new CustomFunction3(f)

  implicit final class FromFunction3[-T1, -T2, -T3, +U](
                                                         f: (T1, T2, T3) => U
                                                       )
                                                       (implicit
                                                        encoder1: Encoder[T1],
                                                        encoder2: Encoder[T2],
                                                        encoder3: Encoder[T3],
                                                        decoder: Decoder[U]) {
    def asCustomFunction: CustomFunction3[T1, T2, T3, U] = CustomFunction3(f)
  }

}
