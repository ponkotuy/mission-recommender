package utils


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
    Bearing(if(raw < 0 ) raw + 360 else raw)
  }
}

case class Bearing(value: Double) {
  def points16 = ((value + (360.0 / 32.0)) / (360.0 / 16.0)).toInt match {
    case 0 => "N"
    case 1 => "NNE"
    case 2 => "NE"
    case 3 => "ENE"
    case 4 => "E"
    case 5 => "ESE"
    case 6 => "SE"
    case 7 => "SSE"
    case 8 => "S"
    case 9 => "SSW"
    case 10 => "SW"
    case 11 => "WSW"
    case 12 => "W"
    case 13 => "WNW"
    case 14 => "NW"
    case 15 => "NNW"
  }
}
