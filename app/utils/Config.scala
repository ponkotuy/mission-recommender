package utils

import play.api.Configuration

class Config(orig: Configuration) {
  lazy val googleMapsKey = orig.get[String]("google.maps.key")
}
