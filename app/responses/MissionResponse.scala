package responses

import models.MissionState
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, Reads}
import play.api.libs.ws.WSClient
import utils.{Get, Location, Region}

import scala.concurrent.{ExecutionContext, Future}

case class MissionResponse(rid: Int, mission: Seq[JSMission])

object MissionResponse {
  import JSMission.missionReads

  implicit val responseReads: Reads[MissionResponse] = (
      (JsPath \ "rid").read[Int] and
      (JsPath \ "mission").read[Seq[JSMission]]
    )(MissionResponse.apply _)

  def get(params: Map[String, String])(implicit ec: ExecutionContext, ws: WSClient): Future[MissionResponse] = {
    val url = Get.withParams(s"${IngressMM.BaseURL}get_mission.php")(params)
    ws.url(url).get().map { res =>
      val json = Json.parse(res.body)
      json.validate[MissionResponse].get
    }
  }

  def get(location: Location, region: Region)(implicit ec: ExecutionContext, ws: WSClient): Future[MissionResponse] = {
    val params = Map[String, String](
      "center[lat]" -> location.lat.toString,
      "center[lng]" -> location.lng.toString,
      "bounds[ne][lat]" -> region.north.toString,
      "bounds[ne][lng]" -> region.east.toString,
      "bounds[sw][lat]" -> region.south.toString,
      "bounds[sw][lng]" -> region.west.toString,
      "rid" -> "10441"
    )
    get(params)
  }

  def find(name: String)(implicit ec: ExecutionContext, ws: WSClient): Future[MissionResponse] =
    get(Map("find" -> name, "findby" -> "0", "rid" -> "10441"))
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
    portals: Seq[Portal]) extends MissionWithPortals {
  override def state: Option[MissionState] = None
}

object JSMission {
  implicit val missionReads: Reads[JSMission] = (
      (JsPath \ "id").read[String].map(_.toInt) and
          (JsPath \ "name").read[String] and
          (JsPath \ "intro").read[String] and
          (JsPath \ "latitude").read[Double] and
          (JsPath \ "longitude").read[Double] and
          ((JsPath \ "distance").read[String].map(_.toDouble) or (JsPath \ "distance").read[Double])
      )(JSMission.apply _)
}
