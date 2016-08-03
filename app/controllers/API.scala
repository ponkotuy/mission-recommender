package controllers

import com.google.inject.Inject
import forms.Feedback
import jp.t2v.lab.play2.auth.AuthenticationElement
import models.MissionState
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc.{Controller, Result}
import play.api.{Configuration, Logger}
import responses.{JSMissionWithPortals, MissionResponse}
import scalikejdbc.{AutoSession, DB}
import utils.{Config, GoogleMaps, Location}

import scala.collection.breakOut
import scala.concurrent.{ExecutionContext, Future}

class API @Inject()(implicit ec: ExecutionContext, ws: WSClient, config: Configuration) extends Controller with AuthenticationElement with AuthConfigImpl {
  lazy val conf = new Config(config)
  lazy val maps = new GoogleMaps(conf.googleMapsKey)

  def missions(lat: Double, lng: Double, meter: Int, q: String) = AsyncStack { implicit req =>
    val user = loggedIn
    val here = Location(lat, lng)
    if(q.isEmpty) localMissions(user.id, here, meter) else searchMissions(user.id, here, q)
  }

  private def localMissions(userId: Long, here: Location, meter: Int): Future[Result] = {
    val region = here.regionFromMeter(meter)
    MissionResponse.get(here, region).flatMap { mRes => createResult(mRes, userId, here) }
  }

  private def searchMissions(userId: Long, here: Location, q: String): Future[Result] = {
    MissionResponse.find(q).flatMap( mRes => createResult(mRes, userId, here))
  }

  private def createResult(mRes: MissionResponse, userId: Long, here: Location): Future[Result] = {
    import responses.Recommend.recommendWrites
    val fromDBs = mRes.mission.flatMap(_.withPortalFromDB(userId)(AutoSession))
    val exists: Set[Int] = fromDBs.map(_.id)(breakOut)
    val fromWebs = mRes.mission.filterNot { m => exists.contains(m.id) }
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

  private def futureRecover(f: Future[Result]): Future[Result] = f.recover { case e =>
    Logger.warn("Raise error in future", e)
    InternalServerError(e.getMessage)
  }

  def missionClear(id: Int) = StackAction { implicit req =>
    val user = loggedIn
    MissionState.updateClear(id, user.id)(AutoSession)
    Ok("Success")
  }

  def missionFeedback(id: Int) = StackAction { implicit req =>
    val user = loggedIn
    Feedback.form.bindFromRequest().fold(
      _ => BadRequest("Required feedback parameter"),
      f => {
        f.feedback.foreach(MissionState.updateFeedback(id, user.id, _)(AutoSession))
        f.notFound.foreach(MissionState.updateNotFound(id, user.id, _)(AutoSession))
        Ok("Success")
      }
    )
  }

  def location(name: String) = StackAction { _ =>
    maps.geocoding.request(name).headOption.fold(NotFound("Not found")) { geo =>
      val location = geo.geometry.location
      Ok(Json.obj("latitude" -> location.lat, "longitude" -> location.lng))
    }
  }
}
