package utils

import org.scalatest.FunSuite
import org.scalacheck._

class BearingSuite extends FunSuite {
  test("Bearing return points16") {
    val angleDegrees = Gen.choose(0.0, 360.0)
    Prop.forAll(angleDegrees) { (degree: Double) =>
      Bearing.values.contains(Bearing(degree).points16)
    }.check
  }
}
