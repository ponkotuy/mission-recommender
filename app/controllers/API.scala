package controllers

import com.google.inject.Inject
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, Controller}
import responses.MissionResponse
import scalikejdbc.{AutoSession, DB}
import utils.Location

import scala.collection.breakOut
import scala.concurrent.{ExecutionContext, Future}

class API @Inject()(implicit ec: ExecutionContext, ws: WSClient) extends Controller {
  import responses.Recommend.recommendWrites
  def missions(lat: Double, lng: Double, meter: Int) = Action.async {
    val here = Location(lat, lng)
    val region = here.regionFromMeter(meter)
    val res = for {
      mRes <- MissionResponse.get(here, region)
      fromDBs = mRes.mission.flatMap(_.withPortalFromDB()(AutoSession))
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
}
