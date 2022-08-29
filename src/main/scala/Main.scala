import Topl.{Intersection, ResultantRoute}
import play.api.libs.json.Json

object Main extends App {
  case class CleanerArgs(dataFilePath: os.Path, startingIntersection: Intersection, endingIntersection: Intersection)

  validateArgs() match {
    case Left(errorMessage) => println(errorMessage)
    case Right(cleanerArgs) =>
      val data           = Topl.Traffic.loadTrafficDataFromFile(cleanerArgs.dataFilePath)
      val normalizedData = Topl.Traffic.normalizeTrafficData(data)
      val bestRoute = Topl.Traffic.findBestRoute(
        startingIntersection = cleanerArgs.startingIntersection,
        endingIntersection = cleanerArgs.endingIntersection,
        normalizedData
      )

      // Output
      println(
        Json.toJson(
          ResultantRoute(
            startingInterestion = cleanerArgs.startingIntersection,
            endingIntersection = cleanerArgs.endingIntersection,
            paths = bestRoute.paths,
            totalTime = bestRoute.transitTime
          )
        )
      )
  }

  def validateArgs(): Either[String, CleanerArgs] = {
    if (args.length == 5) {
      if (os.exists(os.Path(args(0)))) {
        // Now check the intersection arguments
        try {
          val startingIntersection = Intersection(args(1), args(2))
          val endingIntersection   = Intersection(args(3), args(4))
          Right(
            CleanerArgs(
              os.Path(args(0)),
              startingIntersection = startingIntersection,
              endingIntersection = endingIntersection
            )
          )
        } catch {
          case ex: Exception => Left(s"Bad intersection arguments: ${ex.getMessage}")
        }
      } else
        Left(s"The path ${args(0)} doesn't exist")
    } else
      Left(
        "Not enough arguments. Please use this arguments in this format:\nDatafilePath StartingAvenue StartingStreet EndingAvenue EndingStreet"
      )
  }
}
