package responses

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, Reads}
import play.api.libs.ws.WSClient
import utils.{Get, Location}

import scala.concurrent.{ExecutionContext, Future}

case class MissionResponse(rid: Int, mission: Seq[JSMission])

object MissionResponse {
  import JSMission.missionReads

  implicit val responseReads: Reads[MissionResponse] = (
      (JsPath \ "rid").read[Int] and
      (JsPath \ "mission").read[Seq[JSMission]]
    )(MissionResponse.apply _)

  val DiffLat = 0.002
  val DiffLng = 0.003
  def get(location: Location)(implicit ex: ExecutionContext, ws: WSClient): Future[MissionResponse] = {
    val params = Map[String, String](
      "center[lat]" -> location.lat.toString,
      "center[lng]" -> location.lng.toString,
      "bounds[ne][lat]" -> (location.lat + DiffLat).toString,
      "bounds[ne][lng]" -> (location.lng + DiffLng).toString,
      "bounds[sw][lat]" -> (location.lat - DiffLat).toString,
      "bounds[sw][lng]" -> (location.lng - DiffLng).toString,
      "rid" -> "10441"
    )
    val url = Get.withParams(s"${IngressMM.BaseURL}get_mission.php")(params)
    ws.url(url).get().map { res =>
      val json = Json.parse(res.body)
      json.validate[MissionResponse].get
    }
  }
}

case class JSMission(
    id: Int,
    name: String,
    intro: String,
    latitude: Double,
    longitude: Double,
    distance: Double) extends Mission {
  def withPortals(portals: Seq[Portal]) = JSMissionWithPortals(id, name, intro, latitude, longitude, distance, portals)

  def withPortalFromWeb()(implicit ex: ExecutionContext, ws: WSClient): Future[JSMissionWithPortals] = {
    getPortal().map { pRes =>
      withPortals(pRes.portal)
    }
  }
}

case class JSMissionWithPortals(
    id: Int,
    name: String,
    intro: String,
    latitude: Double,
    longitude: Double,
    distance: Double,
    portals: Seq[Portal]) extends MissionWithPortals

object JSMission {
  implicit val missionReads: Reads[JSMission] = (
      (JsPath \ "id").read[String].map(_.toInt) and
          (JsPath \ "name").read[String] and
          (JsPath \ "intro").read[String] and
          (JsPath \ "latitude").read[Double] and
          (JsPath \ "longitude").read[Double] and
          (JsPath \ "distance").read[String].map(_.toDouble)
      )(JSMission.apply _)
}
