package com.github.simonthecat.imagelibrary.http.auth

import spray.routing.authentication.UserPass
import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global

object UserPassAuth {
  def apply(userPass: Option[UserPass]): Future[Option[User]] = userPass match {
    case Some(up) =>
      Future {
        if (up.user == "test" && up.pass == "pass")
          Some(User("test"))
        else
          None
      }
    case None => Future.successful(None)
  }
}
