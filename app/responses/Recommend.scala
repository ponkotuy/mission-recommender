package responses

import play.api.libs.json.{Json, Writes}


case class Recommend(
    id: Int,
    name: String,
    to: Double,
    around: Double,
    isClear: Boolean,
    notFound: Boolean,
    feedback: Int,
    bearing: String,
    portals: Seq[Portal]
) extends Ordered[Recommend] {
  override def compare(that: Recommend): Int = Recommend.ordering.compare(this, that)
}

object Recommend {
  implicit val recommendWrites = new Writes[Recommend] {
    def writes(x: Recommend) = Json.obj(
      "id" -> x.id,
      "name" -> x.name,
      "to" -> x.to,
      "around" -> x.around,
      "isClear" -> x.isClear,
      "notFound" -> x.notFound,
      "feedback" -> x.feedback,
      "bearing" -> x.bearing,
      "portals" -> x.portals
    )
  }

  val ordering: Ordering[Recommend] = Ordering.by { r =>
    if(r.isClear || r.feedback < 0 || r.notFound) Double.MaxValue else if(r.feedback > 0) 0 else r.to * 2 + r.around
  }
}
