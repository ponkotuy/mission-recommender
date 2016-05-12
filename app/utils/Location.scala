package utils

import scala.collection.immutable


case class Location(lat: Double, lng: Double) {
  def distance(other: Location): Double = Distance.periodLocation(this, other)

  def vector(to: Location) = {
    val toLat = to.lat - lat
    val toLng = to.lng - lng
    Vector(Distance.latToMeter(toLat), Distance.lngToMeter(toLat, toLng))
  }

  def regionFromMeter(meter: Int) = {
    val pLat = Distance.fromMeterToLatitude(meter)
    val pLng = Distance.fromMeterToLongitude(meter, lat)
    Region(lat + pLat, lat - pLat, lng + pLng, lng - pLng)
  }
}

case class Region(north: Double, south: Double, east: Double, west: Double)

case class Vector(latMeter: Double, lngMeter: Double) {
  lazy val distance = math.sqrt(latMeter * latMeter + lngMeter * lngMeter)
  def bearing = {
    val raw = math.atan2(lngMeter, latMeter) * 180 / math.Pi
    Bearing(if(raw < 0) raw + 360 else raw)
  }
}

/** 0 <= value <= 360 */
case class Bearing(value: Double) {
  def points16 = Bearing.values(((value + (360.0 / 32.0)) / (360.0 / 16.0)).toInt)
}

object Bearing {
  val values = immutable.Vector("N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW", "N")
}
