package com.github.simonthecat.imagelibrary.http.auth

import org.apache.logging.log4j.LogManager
import spray.routing.authentication.UserPass
import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global
import com.github.simonthecat.imagelibrary.core.security.Security

class UserPassAuth(implicit security: Security) {

  val log = LogManager.getLogger(this.getClass)

  def login(userPass: Option[UserPass]): Future[Option[User]] = {
    userPass match {
      case Some(up) =>
        security.login(up.user, up.pass).map(_.map(su => User(su.username)))
      case None => Future.successful(None)
    }
  }

}
