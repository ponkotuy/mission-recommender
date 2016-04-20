package utils

import java.security.MessageDigest

import models.Account

import scala.util.Random

object Tools {
  def createAccount(name: String, password: String): Unit = {
    var salt = new Array[Byte](32)
    Random.nextBytes(salt)
    Account.createWithAttributes('name -> name, 'hash -> toHash(password, salt), 'salt -> salt, 'created -> System.currentTimeMillis())
  }

  val StretchCount = 1771
  def toHash(pass: String, salt: Array[Byte]): Array[Byte] = {
    val sha256 = MessageDigest.getInstance("SHA-256")
    var result = pass.getBytes
    (1 to StretchCount).foreach { _ =>
      result = sha256.digest(result ++ salt)
    }
    result
  }
}
