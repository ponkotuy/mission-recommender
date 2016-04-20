package controllers

import com.google.inject.Inject
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, Controller}
import responses.{Mission, MissionResponse}
import scalikejdbc.{AutoSession, DB}
import utils.Location

import scala.collection.breakOut
import scala.concurrent.{ExecutionContext, Future}

class Root @Inject()(implicit ec: ExecutionContext, ws: WSClient) extends Controller {
  def index() = Action.async {
    val here = Location(35.68198334797145, 139.76821370290222)
    for {
      mRes <- MissionResponse.get(here)
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
      val contents = (fromDBs ++ xs)
          .sortBy { x =>  }
          .map { x => s"${x.name}: first = ${here.distance(x.location)}m, total = ${x.portalDistance}m" }
          .mkString("\n")
      Ok(contents)
    }
  }
}
