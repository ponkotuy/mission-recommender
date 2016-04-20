package responses

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, Reads}
import play.api.libs.ws.WSClient
import utils.Get

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

case class PortalResponse(mission: Int, portal: Seq[JSPortal])

object PortalResponse {
  import JSPortal.portalReads

  implicit val responseReads: Reads[PortalResponse] = (
      (JsPath \ "mission").read[Int] and
          (JsPath \ "portal").read[Seq[JSPortal]]
      )(PortalResponse.apply _)

  def get(mission: Int)(implicit ex: ExecutionContext, ws: WSClient): Future[PortalResponse] = {
    val params = Map("mission" -> mission.toString)
    val url = Get.withParams(s"${IngressMM.BaseURL}get_portal.php")(params)
    ws.url(url).get().flatMap { res =>
      val json = Json.parse(res.body)
      Future.fromTry(Try(json.validate[PortalResponse].get))
    }
  }
}

case class JSPortal(id: Int, name: String, latitude: Double, longitude: Double, typ: String) extends Portal

object JSPortal {
  implicit val portalReads: Reads[JSPortal] = (
      (JsPath(2) \ "id").read[String].map(_.toInt) and
          (JsPath(2) \ "name").read[String] and
          (JsPath(2) \ "latitude").read[Double] and
          (JsPath(2) \ "longitude").read[Double] and
          (JsPath(2) \ "type").read[String]
      )(JSPortal.apply _)
}
