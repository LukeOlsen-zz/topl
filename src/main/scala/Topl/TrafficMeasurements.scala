package Topl

// Classes used for importing source json data

import upickle.default.{ReadWriter => RW, macroRW}

case class TrafficMeasurements(
    trafficMeasurements: List[TrafficMeasurement]
)
object TrafficMeasurements {
  implicit val rw: RW[TrafficMeasurements] = macroRW
}

case class TrafficMeasurement(
    measurementTime: Long,
    measurements: List[Measurement]
)
object TrafficMeasurement {
  implicit val rw: RW[TrafficMeasurement] = macroRW
}

case class Measurement(
    startAvenue: String,
    startStreet: Int,
    transitTime: Float,
    endAvenue: String,
    endStreet: Int
)
object Measurement {
  implicit val rw: RW[Measurement] = macroRW
}
