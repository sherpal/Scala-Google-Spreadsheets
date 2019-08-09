package tutorial.customfunctions

import cells.Cell._
import cells.customfunctions.{Encoder, Output}

import scala.util.Try

final case class Foo(bar: String, babar: Int)

object Foo {
  implicit val fooEncoder: Encoder[Vector[Foo]] =
    (data: Output) => Try(
      data.asScala.filterNot(_(0).isEmpty).map(v => Foo(v(0).toString, v(1).toInt.get))
    )

}