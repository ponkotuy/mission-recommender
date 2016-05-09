package responses

import models.{MissionPortal, MissionState, RDBMission}
import play.api.libs.json.{JsValue, Json, Writes}
import play.api.libs.ws.WSClient
import scalikejdbc.DBSession
import utils.Location

import scala.concurrent.{ExecutionContext, Future}

trait Mission {
  def id: Int
  def name: String
  def intro: String
  def latitude: Double
  def longitude: Double
  lazy val location: Location = Location(latitude, longitude)

  def withPortalFromDB(userId: Long)(implicit session: DBSession): Option[RDBMission] =
    RDBMission.joins(RDBMission.portalsRef).joins(RDBMission.stateRef(userId)).findById(id).filter(_.portals.nonEmpty)

  def getPortal()(implicit ex: ExecutionContext, ws: WSClient): Future[PortalResponse] = PortalResponse.get(id)

  def saveIgnore()(implicit session: DBSession): Unit = {
    if(RDBMission.where('id -> id).count() == 0) save()
  }

  def save()(implicit session: DBSession) =
    RDBMission.createWithAttributes('id -> id, 'name -> name, 'intro -> intro, 'latitude -> latitude, 'longitude -> longitude)
}

object Mission {
  implicit val missionWrites: Writes[Mission] = new Writes[Mission] {
    override def writes(x: Mission): JsValue = Json.obj(
      "id" -> x.id,
      "name" -> x.name,
      "intro" -> x.intro,
      "latitude" -> x.latitude,
      "longitude" -> x.longitude
    )
  }
}

trait MissionWithPortals extends Mission {
  def portals: Seq[Portal]
  def state: Option[MissionState]

  lazy val portalDistance = portals.sliding(2).filterNot(_.size < 2).map { case Seq(x, y) =>
    x.location.distance(y.location)
  }.sum

  def savePortals()(implicit session: DBSession) = {
    portals.foreach(_.saveIgnore())
    portals.zipWithIndex.foreach { case (p, i) =>
      MissionPortal.createWithAttributes('missionId -> id, 'portalId -> p.id, 'no -> i)
    }
  }

  def recommend(here: Location) =
    Recommend(
      id,
      name,
      here.distance(location),
      portalDistance,
      state.exists(_.isClear),
      state.map(_.feedback).getOrElse(0),
      here.vector(location).bearing.points16
    )
}
