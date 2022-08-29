package Topl

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Writes}

case class Path(start: Intersection, end: Intersection, transitTime: Float)
object Path {
  implicit val pathWrites: Writes[Path] = (
    (JsPath \ "start").write[Intersection] and
      (JsPath \ "end").write[Intersection] and
      (JsPath \ "transitTime").write[Float]
  )(unlift(Path.unapply))
}
