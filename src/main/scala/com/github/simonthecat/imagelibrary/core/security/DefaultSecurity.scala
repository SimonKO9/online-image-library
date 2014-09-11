package com.github.simonthecat.imagelibrary.core.security

import java.security.MessageDigest

import org.apache.logging.log4j.LogManager
import sun.misc.BASE64Encoder

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DefaultSecurity(implicit userStorage: UserStorage) extends Security {

  val log = LogManager.getLogger(this.getClass)

  override def hash(salt: String, password: String): String = {
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest((salt + password).getBytes("UTF-8"))
    new BASE64Encoder().encode(digest)
  }

  def checkCredentials(password: String, user: StoredUser): Boolean = {
    val enteredHash = hash(user.salt, password)
    enteredHash == user.passwordHash
  }

  override def login(username: String, password: String): Future[Option[StoredUser]] = {
    userStorage.getUser(username) map {
      case userOpt@Some(user) =>
        if (checkCredentials(password, user)) userOpt
        else None
      case None => None
    }
  }

}
