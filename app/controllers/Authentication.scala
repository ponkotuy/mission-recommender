package controllers

import forms.Login
import models.Account
import play.api.mvc.{Cookie, RequestHeader, Result, Session}
import play.filters.csrf.CSRF
import scalikejdbc.{AutoSession, DB}
import utils.Tools

import scala.util.Random
import scala.concurrent.duration._

object Authentication {
  import play.api.mvc.Results._

  val random = new Random(new java.security.SecureRandom)

  def getAccount(session: Session): Either[Result, Account] = DB autoCommit { implicit db =>
    val res = for {
      token <- session.get("token")
      session <- models.Session.findByToken(token)
      account <- Account.findById(session.accountId)
    } yield account
    res.toRight(Forbidden("Authentication failed. Please login."))
  }

  def authenticate(login: Login)(implicit req: RequestHeader): Result = {
    Account.where('name -> login.name).apply().headOption.flatMap { account =>
      if(Tools.toHash(login.password, account.salt) sameElements account.hash) {
        val now = System.currentTimeMillis()
        val session = new models.Session(0L, account.id, generateToken(), now, now + 7.days.toMillis)
        session.save()(AutoSession)
        Some(Redirect("/").withSession(session.toSession:_*))
      } else None
    }.getOrElse(Unauthorized("Authentication failed"))
  }

  def csrfCookie()(implicit req: RequestHeader): Option[Cookie] = CSRF.getToken(req).map { token =>
    Cookie("CSRF_TOKEN", token.value, secure = false, httpOnly = false)
  }

  val Chars = "abcdefghijklmnopqrstuvwxyz_0123456789"
  private def generateToken(): String = {
    (0 until 64).map { _ => random.nextInt(Chars.length) }.map(Chars.charAt).mkString
  }
}
