package responses

import models.RDBPortal
import play.api.libs.json.{JsValue, Json, Writes}
import scalikejdbc.DBSession
import utils.Location

trait Portal {
  def id: Int
  def name: String
  def latitude: Double
  def longitude: Double
  def typ: String
  lazy val location = Location(latitude, longitude)

  def saveIgnore()(implicit session: DBSession): Unit = {
    if(RDBPortal.where('id -> id).count() == 0) save()
  }

  def save()(implicit session: DBSession) = {
    RDBPortal.createWithAttributes('id -> id, 'name -> name, 'latitude -> latitude, 'longitude -> longitude, 'typ -> typ)
  }
}
object Portal {
  implicit val portalWrites = new Writes[Portal] {
    override def writes(p: Portal): JsValue = Json.obj(
      "id" -> p.id,
      "name" -> p.name,
      "latitude" -> p.latitude,
      "longitude" -> p.longitude,
      "type" -> p.typ
    )
  }
}
