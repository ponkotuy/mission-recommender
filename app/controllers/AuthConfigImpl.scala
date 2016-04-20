package controllers

import jp.t2v.lab.play2.auth.AuthConfig
import models.Account
import play.api.mvc.Results._
import play.api.mvc.{RequestHeader, Result}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.reflect._

trait AuthConfigImpl extends AuthConfig {
  override type Id = Long
  override type User = Account
  override type Authority = Unit
  override val idTag: ClassTag[Id] = classTag[Id]
  override val sessionTimeoutInSeconds = 7.days.toSeconds.toInt

  override def resolveUser(id: Id)(implicit ec: ExecutionContext): Future[Option[User]] =
    Future.successful(Account.findById(id))

  override def authorizationFailed(request: RequestHeader, user: User, authority: Option[Authority])(implicit ec: ExecutionContext): Future[Result] =
    Future.successful(Forbidden("No permission"))

  override def authorize(user: User, authority: Authority)(implicit ec: ExecutionContext): Future[Boolean] =
    Future.successful(true)

  override def loginSucceeded(request: RequestHeader)(implicit ec: ExecutionContext): Future[Result] =
    Future.successful(Redirect("/"))

  override def logoutSucceeded(request: RequestHeader)(implicit ec: ExecutionContext): Future[Result] =
    Future.successful(Ok("Success"))

  override def authenticationFailed(request: RequestHeader)(implicit ec: ExecutionContext): Future[Result] =
    Future.successful(Forbidden("Authentication failed"))
}
