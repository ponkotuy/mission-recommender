package responses

import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.libs.ws.WSClient
import utils.Get

import scala.concurrent.{ExecutionContext, Future}

case class PortalResponse(mission: Int, portal: Seq[JSPortal])

object PortalResponse {
  import JSPortal.portalReads

  implicit val responseReads: Reads[PortalResponse] = (
      (JsPath \ "mission").read[Int] and
          (JsPath \ "portal").read[Seq[WrappedJsPortal]].map(_.flatMap(_.content))
      )(PortalResponse.apply _)

  def get(mission: Int)(implicit ex: ExecutionContext, ws: WSClient): Future[PortalResponse] = {
    val params = Map("mission" -> mission.toString)
    val url = Get.withParams(s"${IngressMM.BaseURL}get_portal.php")(params)
    ws.url(url).get().map { res =>
      val json = Json.parse(res.body)
      json.validate[PortalResponse].getOrElse(throw new JSONParseError(res.body, getClass.getSimpleName))
    }
  }
}

case class JSPortal(id: Int, name: String, latitude: Double, longitude: Double, typ: String) extends Portal

object JSPortal {
  implicit val portalReads: Reads[JSPortal] = (
      (JsPath \ "id").read[String].map(_.toInt) and
          (JsPath \ "name").read[String] and
          (JsPath \ "latitude").read[Double] and
          (JsPath \ "longitude").read[Double] and
          (JsPath \ "type").read[String]
      )(JSPortal.apply _)
}

case class WrappedJsPortal(b: Boolean, i: Option[Int], content: Option[JSPortal])

object WrappedJsPortal {
  implicit val wrapReads: Reads[WrappedJsPortal] = (
      JsPath(0).read[Boolean] and
          JsPath(1).readNullable[Int] and
          JsPath(2).readNullable[JSPortal]
      )(WrappedJsPortal.apply _)
}
