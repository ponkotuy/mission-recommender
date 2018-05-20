package controllers

import forms.Login
import javax.inject.Inject
import play.api.mvc.{InjectedController, MessagesActionBuilder}

import scala.concurrent.ExecutionContext

class SessionController @Inject()(implicit val ec: ExecutionContext, messageAction: MessagesActionBuilder) extends InjectedController {
  def view() = messageAction { implicit req =>
    Ok(views.html.login(Login.form))
  }

  def login() = Action { implicit req =>
    Login.form.bindFromRequest().fold(
      errors => BadRequest(errors.errors.mkString("\n")),
      Authentication.authenticate
    )
  }
}
