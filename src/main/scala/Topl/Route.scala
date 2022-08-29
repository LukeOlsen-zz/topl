package Topl

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Writes}

sealed trait RouteStatus
object RouteStatus {
  case object Cyclic       extends RouteStatus
  case object Undetermined extends RouteStatus
  case object Finished     extends RouteStatus
}

case class Route(status: RouteStatus, paths: List[Path], transitTime: Option[Float])

case class ResultantRoute(
    startingInterestion: Intersection,
    endingIntersection: Intersection,
    paths: List[Path],
    totalTime: Option[Float]
)
object ResultantRoute {
  implicit val routeWrites: Writes[ResultantRoute] = (
    (JsPath \ "startingIntersection").write[Intersection] and
      (JsPath \ "endingIntersection").write[Intersection] and
      (JsPath \ "paths").write[List[Path]] and
      (JsPath \ "totalTime").write[Option[Float]]
  )(unlift(ResultantRoute.unapply))
}
