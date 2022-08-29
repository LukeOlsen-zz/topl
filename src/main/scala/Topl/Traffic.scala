package Topl

import upickle.default._

import scala.annotation.tailrec
object Traffic {

  def loadTrafficDataFromFile(
      filePath: os.ReadablePath
  ): TrafficMeasurements = {
    val rawJson = os.read(filePath)
    read[TrafficMeasurements](rawJson)
  }

  def normalizeTrafficData(
      trafficMeasurements: TrafficMeasurements
  ): List[Path] = {
    // We have multiple measurements per transit. We will need to return an average that
    // will be the basis for our route selection

    // Note: This can be further enhancements may include an outlier filter, rolling average, etc...

    // Measurement time invariant
    val allData =
      trafficMeasurements.trafficMeasurements.flatMap(_.measurements)

    // At this point we have potentially multiple values per transit
    // Key to make our code a little more readable
    case class measurementKey(
        startAvenue: String,
        startStreet: Int,
        endAvenue: String,
        endStreet: Int
    )
    val avgData = allData
      .groupMap(key =>
        measurementKey(
          startAvenue = key.startAvenue,
          startStreet = key.startStreet,
          endAvenue = key.endAvenue,
          endStreet = key.endStreet
        )
      )(
        _.transitTime
      )
      .map(a =>
        Path(
          start = Intersection(a._1.startAvenue, a._1.startStreet),
          end = Intersection(a._1.endAvenue, a._1.endStreet),
          transitTime = a._2.sum / a._2.length
        )
      )

    avgData.toList
  }

  def findBestRoute(
      startingIntersection: Intersection,
      endingIntersection: Intersection,
      routeMap: List[Path]
  ): Route = {

    @tailrec
    def getRoutes(
        routes: List[Route],
        endingIntersection: Intersection,
        searchLimit: Int,
        determinedRoutes: List[Route]
    ): List[Route] = {
      val candidateRoutes = routes
        .filter(a => a.status == RouteStatus.Undetermined)
        .flatMap { route =>
          // Get the next path from the last path for each route
          val nextPathsViaThisRoute = routeMap.filter(r => r.start == route.paths.last.end)

          // Make determination to keep going on a path
          nextPathsViaThisRoute.map { p =>
            val alreadyTravelled: Boolean     = route.paths.exists(alreadyTravelled => alreadyTravelled.start == p.end)
            val atEndingIntersection: Boolean = p.end == endingIntersection
            val statusValue: RouteStatus = {
              if (atEndingIntersection)
                RouteStatus.Finished
              else if (alreadyTravelled) RouteStatus.Cyclic
              else RouteStatus.Undetermined
            }

            Route(
              statusValue,
              route.paths ++ List(
                Path(
                  start = p.start,
                  end = p.end,
                  p.transitTime
                )
              ),
              transitTime = Some(route.transitTime.getOrElse(0f) + p.transitTime)
            )
          }
        }
        .distinct

      val (wip, newDetermined) = candidateRoutes.partition(r => r.status == RouteStatus.Undetermined)
      val determined           = newDetermined ++ determinedRoutes

      if (searchLimit > 15 || wip.isEmpty) {
        determined
      } else
        getRoutes(wip, endingIntersection, searchLimit + 1, determined)
    }

    if (startingIntersection != endingIntersection) {
      // We may have multiple starting paths. Build this first starting set
      val startingRoutes = routeMap
        .filter(r => r.start == startingIntersection)
        .map(r =>
          Route(
            RouteStatus.Undetermined,
            List(
              Path(
                start = r.start,
                end = r.end,
                r.transitTime
              )
            ),
            transitTime = Some(r.transitTime)
          )
        )

      // Are any of these starting routes the result we want?
      val routeExistsAtStart = startingRoutes.filter(a => a.paths.last.end == endingIntersection)
      if (routeExistsAtStart.isEmpty) {
        val finishedRoutes = getRoutes(
          startingRoutes,
          endingIntersection = endingIntersection,
          0,
          determinedRoutes = List.empty
        ).filter(a => a.status == RouteStatus.Finished)

        if (finishedRoutes.isEmpty)
          Route(RouteStatus.Undetermined, List.empty, None)
        else
          finishedRoutes.minBy(_.transitTime)
      } else
        Route(RouteStatus.Finished, routeExistsAtStart.head.paths, routeExistsAtStart.head.transitTime)
    } else {
      // If requested start and end intersection are the same return immediately
      Route(RouteStatus.Finished, List(Path(startingIntersection, endingIntersection, 0)), Some(0f))
    }

  }

}
