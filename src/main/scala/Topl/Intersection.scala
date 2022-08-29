package Topl

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Writes}

case class Intersection(avenue: String, street: Int)

object Intersection {
  implicit val writes: Writes[Intersection] = (
    (JsPath \ "avenue").write[String] and
      (JsPath \ "street").write[Int]
  )(unlift(Intersection.unapply))

  def apply(avenue: String, street: String): Intersection = {
    require(avenue.nonEmpty)
    val maybeStreet = street.toIntOption
    Intersection(
      avenue = avenue,
      street = maybeStreet.getOrElse(throw new IllegalArgumentException("Street must be an integer"))
    )
  }
}
