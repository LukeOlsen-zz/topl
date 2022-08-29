package Topl

import scala.concurrent.duration._

class ToplTrafficPathTests extends munit.FunSuite {
  override val munitTimeout: FiniteDuration = 10.seconds

  // Load test data
  val avgTestDataFilePath: os.Path = os.Path(os.pwd + "/src/test/scala/topl/toplTransitTestData.json")
  val data: TrafficMeasurements    = Topl.Traffic.loadTrafficDataFromFile(avgTestDataFilePath)
  val avgData: List[Path]          = Topl.Traffic.normalizeTrafficData(data)

  // delta for avg calcs
  val delta = 0.00001f

  test("data existence") {
    assert(data.trafficMeasurements.nonEmpty)
  }

  test("do we have avg data") {
    assert(avgData.nonEmpty)
  }

  test("handle when start and end are the same") {
    val startInterestion = Intersection("A", 1)
    val endIntersection  = Intersection("A", 1)
    val result           = Topl.Traffic.findBestRoute(startInterestion, endIntersection, avgData)
    val expectation      = Route(RouteStatus.Finished, List(Path(startInterestion, endIntersection, 0)), Some(0f))

    assertEquals(result, expectation)
  }

  test("non-existent path - no solution") {
    val startInterestion = Intersection("A", 1)
    val endIntersection  = Intersection("ZZ", 122)
    val result           = Topl.Traffic.findBestRoute(startInterestion, endIntersection, avgData)
    val expectation      = Route(RouteStatus.Undetermined, List.empty, None)

    assertEquals(result, expectation)
  }

  test("cyclic path - make sure we ignore and still get the correct path") {
    val startInterestion = Intersection("A", 1)
    val endIntersection  = Intersection("F", 1)
    val result           = Topl.Traffic.findBestRoute(startInterestion, endIntersection, avgData)
    val path1            = Path(startInterestion, Intersection("C", 1), 20f)
    val path2            = Path(Intersection("C", 1), endIntersection, 1f)

    val expectation = Route(RouteStatus.Finished, List(path1, path2), Some(21f))

    assertEquals(result, expectation)
  }

  test("path already presented at start") {
    val startInterestion = Intersection("A", 1)
    val endIntersection  = Intersection("B", 1)
    val result           = Topl.Traffic.findBestRoute(startInterestion, endIntersection, avgData)
    val path1            = Path(startInterestion, endIntersection, 10f)

    val expectation = Route(RouteStatus.Finished, List(path1), Some(10f))

    assertEquals(result, expectation)
  }

  test("best path out of a single choice - only one path exists") {
    val startInterestion = Intersection("F", 1)
    val endIntersection  = Intersection("B", 1)
    val result           = Topl.Traffic.findBestRoute(startInterestion, endIntersection, avgData)
    val path1            = Path(startInterestion, Intersection("G", 1), 9f)
    val path2            = Path(Intersection("G", 1), endIntersection, 5.3f)

    val expectation = Route(RouteStatus.Finished, List(path1, path2), Some(14.3f))

    assertEquals(result, expectation)
  }

  test("best path out of multiple choices") {
    val startInterestion = Intersection("C", 1)
    val endIntersection  = Intersection("H", 5)
    val result           = Topl.Traffic.findBestRoute(startInterestion, endIntersection, avgData)
    val path1            = Path(startInterestion, Intersection("E", 1), 6f)
    val path2            = Path(Intersection("E", 1), Intersection("J", 3), 2f)
    val path3            = Path(Intersection("J", 3), endIntersection, 3f)

    val expectation = Route(RouteStatus.Finished, List(path1, path2, path3), Some(11f))

    assertEquals(result, expectation)
  }

}
