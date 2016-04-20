package responses

import models.{MissionPortal, RDBMission}
import play.api.libs.json.{Json, Writes}
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

  def withPortalFromDB()(implicit session: DBSession): Option[RDBMission] =
    RDBMission.joins(RDBMission.portalsRef).findById(id).filter(_.portals.nonEmpty)

  def getPortal()(implicit ex: ExecutionContext, ws: WSClient): Future[PortalResponse] = PortalResponse.get(id)

  def saveIgnore()(implicit session: DBSession): Unit = {
    if(RDBMission.where('id -> id).count() == 0) save()
  }

  def save()(implicit session: DBSession) =
    RDBMission.createWithAttributes('id -> id, 'name -> name, 'intro -> intro, 'latitude -> latitude, 'longitude -> longitude)
}

trait MissionWithPortals extends Mission {
  def portals: Seq[Portal]

  lazy val portalDistance = portals.sliding(2).map { case Seq(x, y) =>
    x.location.distance(y.location)
  }.sum

  def savePortals()(implicit session: DBSession) = {
    portals.foreach(_.saveIgnore())
    portals.zipWithIndex.foreach { case (p, i) =>
      MissionPortal.createWithAttributes('missionId -> id, 'portalId -> p.id, 'no -> i)
    }
  }

  def recommend(here: Location) = Recommend(name, here.distance(location), portalDistance)
}

case class Recommend(name: String, to: Double, around: Double) extends Ordered[Recommend] {
  override def compare(that: Recommend): Int = Recommend.ordering.compare(this, that)
}

object Recommend {
  implicit val recommendWrites = new Writes[Recommend] {
    def writes(x: Recommend) = Json.obj(
      "name" -> x.name,
      "to" -> x.to,
      "around" -> x.around
    )
  }

  val ordering: Ordering[Recommend] = Ordering.by { r => r.to * 2 + r.around }
}
