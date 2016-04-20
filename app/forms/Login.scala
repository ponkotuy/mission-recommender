package forms

import play.api.data.Form
import play.api.data.Forms._

case class Login(name: String, password: String)

object Login {
  val form = Form(
    mapping(
      "name" -> text,
      "password" -> text
    )(Login.apply)(Login.unapply)
  )
}
