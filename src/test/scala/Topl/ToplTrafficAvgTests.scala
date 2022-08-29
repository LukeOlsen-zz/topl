package Topl

import scala.concurrent.duration._

class ToplTrafficAvgTests extends munit.FunSuite {
  override val munitTimeout: FiniteDuration = 10.seconds

  // Load test data
  val avgTestDataFilePath: os.Path = os.Path(os.pwd + "/src/test/scala/topl/toplAvgTestData.json")
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

  test("we should only have 6 elements in our avg data set") {
    assertEquals(avgData.length, 6)
  }

  test("check avg values for each element") {
    val a1b1 =
      avgData.filter(a => a.start.avenue == "A" && a.start.street == 1 && a.end.avenue == "B" && a.end.street == 1)
    assertEquals(a1b1.length, 1)
    assertEqualsFloat(a1b1.head.transitTime, 33.22f, delta)

    val a2a1 =
      avgData.filter(a => a.start.avenue == "A" && a.start.street == 2 && a.end.avenue == "A" && a.end.street == 1)
    assertEquals(a2a1.length, 1)
    assertEqualsFloat(a2a1.head.transitTime, 14.336f, delta)

    val a2b2 =
      avgData.filter(a => a.start.avenue == "A" && a.start.street == 2 && a.end.avenue == "B" && a.end.street == 2)
    assertEquals(a2b2.length, 1)
    assertEqualsFloat(a2b2.head.transitTime, 22.48f, delta)

    val a3a2 =
      avgData.filter(a => a.start.avenue == "A" && a.start.street == 3 && a.end.avenue == "A" && a.end.street == 2)
    assertEquals(a3a2.length, 1)
    assertEqualsFloat(a3a2.head.transitTime, 30.66666667f, delta)

    val b1b2 =
      avgData.filter(a => a.start.avenue == "B" && a.start.street == 1 && a.end.avenue == "B" && a.end.street == 2)
    assertEquals(b1b2.length, 1)
    assertEqualsFloat(b1b2.head.transitTime, 4.346666667f, delta)

    val e1f2 =
      avgData.filter(a => a.start.avenue == "E" && a.start.street == 1 && a.end.avenue == "F" && a.end.street == 2)
    assertEquals(e1f2.length, 1)
    assertEqualsFloat(e1f2.head.transitTime, 10f, delta)
  }
}
