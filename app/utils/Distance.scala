package utils

object Distance {
  import math._
  val Radius = 6378137.0
  def periodLocation(x: Location, y: Location) =
    Radius * acos(sin(rad(x.lat)) * sin(rad(y.lat)) + cos(rad(x.lat)) * cos(rad(y.lat)) * cos(rad(y.lng) - rad(x.lng)))

  def fromMeterToLatitude(meter: Int): Double = meter / 111000.0
  def radiusFromLatitude(lat: Double): Double = 2.0 * Pi * Radius * cos(rad(lat))
  def fromMeterToLongitude(meter: Int, lat: Double): Double = 360.0 * meter / radiusFromLatitude(lat)

  def latToMeter(lat: Double) = lat * 111000.0
  def lngToMeter(lat: Double, lng: Double) = lng * radiusFromLatitude(lat) / 360.0

  def rad(d: Double) = d * math.Pi / 180.0
}
