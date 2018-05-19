package controllers

import forms.Feedback
import javax.inject.Inject
import models.MissionState
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc.{InjectedController, Result, Results}
import play.api.{Configuration, Logger}
import responses.{JSMissionWithPortals, MissionResponse}
import scalikejdbc.{AutoSession, DB}
import utils.{Config, GoogleMaps, Location}

import scala.collection.breakOut
import scala.concurrent.{ExecutionContext, Future}

class API @Inject()(implicit ec: ExecutionContext, ws: WSClient, config: Configuration) extends InjectedController {
  import API._

  lazy val conf = new Config(config)
  lazy val maps = new GoogleMaps(conf.googleMapsKey)

  def missions(lat: Double, lng: Double, meter: Int, q: String) = Action.async { implicit req =>
    Authentication.getAccount(req.session).map { account =>
      val here = Location(lat, lng)
      val missions = if(q.isEmpty) localMissions(here, meter) else searchMissions(q)
      missions.flatMap( mRes => createResult(mRes, account.id, here))
    }.left.map(Future.successful).merge
  }

  def missionClear(id: Int) = Action{ implicit req =>
    Authentication.getAccount(req.session).map { account =>
      MissionState.updateClear(id, account.id)(AutoSession)
      Ok("Success")
    }.merge
  }

  def missionFeedback(id: Int) = Action { implicit req =>
    Authentication.getAccount(req.session).map { account =>
      Feedback.form.bindFromRequest().fold(
        _ => BadRequest("Required feedback parameter"),
        f => {
          f.feedback.foreach(MissionState.updateFeedback(id, account.id, _)(AutoSession))
          f.notFound.foreach(MissionState.updateNotFound(id, account.id, _)(AutoSession))
          Ok("Success")
        }
      )
    }.merge
  }

  def location(name: String) = Action { _ =>
    maps.geocoding.request(name).headOption.fold(NotFound("Not found")) { geo =>
      val location = geo.geometry.location
      Ok(Json.obj("latitude" -> location.lat, "longitude" -> location.lng))
    }
  }
}

object API {
  import Results._

  private def localMissions(here: Location, meter: Int)(implicit ec: ExecutionContext, ws: WSClient): Future[MissionResponse] = {
    val region = here.regionFromMeter(meter)
    MissionResponse.get(here, region)
  }

  private def searchMissions(q: String)(implicit ec: ExecutionContext, ws: WSClient): Future[MissionResponse] = {
    MissionResponse.find(q)
  }

  private def createResult(mRes: MissionResponse, userId: Long, here: Location)(implicit ec: ExecutionContext, ws: WSClient): Future[Result] = {
    import responses.Recommend.recommendWrites
    val fromDBs = mRes.mission.flatMap(_.withPortalFromDB(userId)(AutoSession))
    val exists: Set[Int] = fromDBs.map(_.id)(breakOut)
    val fromWebs: Seq[Future[JSMissionWithPortals]] = mRes.mission.filterNot { m => exists.contains(m.id) }
        .sortBy(_.distance).take(5)
        .map { m =>
          Thread.sleep(200L)
          m.withPortalFromWeb()
        }
    val res = Future.sequence(fromWebs).map { xs =>
      saveMissions(xs)
      val contents = (fromDBs ++ xs).map(_.recommend(here)).sorted
      Ok(Json.toJson(contents))
    }
    futureRecover(res)
  }

  private def saveMissions(xs: Seq[JSMissionWithPortals]): Unit = {
    DB localTx { implicit session =>
      xs.foreach(_.saveIgnore())
      xs.foreach(_.savePortals())
    }
  }

  private def futureRecover(f: Future[Result])(implicit ec: ExecutionContext): Future[Result] = f.recover { case e =>
    Logger.warn("Raise error in future", e)
    InternalServerError(e.getMessage)
  }

}
