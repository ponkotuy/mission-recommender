package controllers

import com.google.inject.Inject
import jp.t2v.lab.play2.auth.AuthenticationElement
import models.MissionState
import play.api.Configuration
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc.Controller
import responses.MissionResponse
import scalikejdbc.{AutoSession, DB}
import utils.{Geocoding, Location}

import scala.collection.breakOut
import scala.concurrent.{ExecutionContext, Future}

class API @Inject()(implicit ec: ExecutionContext, ws: WSClient, config: Configuration) extends Controller with AuthenticationElement with AuthConfigImpl {
  import responses.Recommend.recommendWrites

  lazy val geocoding = new Geocoding(config.getString("google.maps.key").get)

  def missions(lat: Double, lng: Double, meter: Int) = AsyncStack { implicit req =>
    val user = loggedIn
    val here = Location(lat, lng)
    val region = here.regionFromMeter(meter)
    val res = for {
      mRes <- MissionResponse.get(here, region)
      fromDBs = mRes.mission.flatMap(_.withPortalFromDB(user.id)(AutoSession))
      exists: Set[Int] = fromDBs.map(_.id)(breakOut)
      fromWebs = mRes.mission.filterNot { m => exists.contains(m.id) }
          .sortBy(_.distance).take(5)
          .map { m =>
            Thread.sleep(200L)
            m.withPortalFromWeb()
          }
      xs <- Future.sequence(fromWebs)
    } yield {
      DB localTx { implicit session =>
        mRes.mission.foreach(_.saveIgnore())
        xs.foreach(_.savePortals())
      }
      val contents = (fromDBs ++ xs).map(_.recommend(here)).sorted
      Ok(Json.toJson(contents))
    }
    res.fallbackTo(Future.successful(InternalServerError("Be perhaps over region.")))
  }

  def missionClear(id: Int) = StackAction { implicit req =>
    val user = loggedIn
    MissionState.updateClear(id, user.id)(AutoSession)
    Ok("Success")
  }

  def missionFeedback(id: Int) = StackAction { implicit req =>
    val user = loggedIn
    val form = Form("feedback" -> number)
    form.bindFromRequest().fold(
      _ => BadRequest("Required feedback parameter"),
      f => {
        MissionState.updateFeedback(id, user.id, f)(AutoSession)
        Ok("Success")
      }
    )
  }

  def location(name: String) = StackAction { _ =>
    geocoding.request(name).headOption.fold(NotFound("")) { geo =>
      val location = geo.geometry.location
      Ok(Json.obj("latitude" -> location.lat, "longitude" -> location.lng))
    }
  }
}
