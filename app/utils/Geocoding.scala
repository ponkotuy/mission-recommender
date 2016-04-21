package utils

import com.google.maps.{GeoApiContext, GeocodingApi}

class Geocoding(key: String) {
  private val context = new GeoApiContext().setApiKey(key)

  def request(address: String) =
    GeocodingApi.newRequest(context).address(address).await()
}
