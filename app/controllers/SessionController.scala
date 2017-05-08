package controllers

import com.google.inject.Inject
import forms.Login
import jp.t2v.lab.play2.auth.LoginLogout
import models.Account
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller, RequestHeader, Result}
import utils.Tools

import scala.concurrent.{ExecutionContext, Future}

class SessionController @Inject()(val messagesApi: MessagesApi, implicit val ec: ExecutionContext) extends Controller with LoginLogout with AuthConfigImpl with I18nSupport {
  def view() = Action {
    Ok(views.html.login(Login.form))
  }

  def login() = Action.async { implicit req =>
    Login.form.bindFromRequest().fold(
      errors => Future.successful(BadRequest(errors.errors.mkString("\n"))),
      authenticate
    )
  }

  private def authenticate(login: Login)(implicit req: RequestHeader): Future[Result] = {
    Account.where('name -> login.name).apply().headOption.flatMap { account =>
      if(Tools.toHash(login.password, account.salt) sameElements account.hash) Some(gotoLoginSucceeded(account.id)) else None
    }.getOrElse(Future.successful(Unauthorized("Authentication failed")))
  }
}
