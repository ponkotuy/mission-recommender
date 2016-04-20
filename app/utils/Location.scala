package utils


case class Location(lat: Double, lng: Double) {
  import Location._

  def distance(other: Location): Double = {
    import scala.math._
    Radius * acos(sin(rad(lat)) * sin(rad(other.lat)) + cos(rad(lat)) * cos(rad(other.lat)) * cos(rad(other.lng) - rad(lng)))
  }

  def rad(d: Double) = d * math.Pi / 180
}

object Location {
  val Radius = 6378137.0
}
