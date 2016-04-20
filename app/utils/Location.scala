package utils


case class Location(lat: Double, lng: Double) {
  def distance(other: Location): Double = Distance.periodLocation(this, other)
  def regionFromMeter(meter: Int) = {
    val pLat = Distance.fromMeterToLatitude(meter)
    val pLng = Distance.fromMeterToLongitude(meter, lat)
    Region(lat + pLat, lat - pLat, lng + pLng, lng - pLng)
  }
}

case class Region(north: Double, south: Double, east: Double, west: Double)
