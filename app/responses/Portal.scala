package responses

import models.RDBPortal
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
