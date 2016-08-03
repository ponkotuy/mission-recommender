package forms

import play.api.data.Form
import play.api.data.Forms._

case class Feedback(feedback: Option[Int], notFound: Option[Boolean])

object Feedback {
  val form = Form(
    mapping(
      "feedback" -> optional(number),
      "notFound" -> optional(boolean)
    )(Feedback.apply)(Feedback.unapply)
  )
}
