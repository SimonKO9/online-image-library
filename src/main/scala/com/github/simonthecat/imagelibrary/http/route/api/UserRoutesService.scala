package com.github.simonthecat.imagelibrary.http.route.api

import com.github.simonthecat.imagelibrary.core.security.UserStorage
import com.github.simonthecat.imagelibrary.http.auth.User
import com.github.simonthecat.imagelibrary.http.route.ExtraDirectives
import com.github.simonthecat.imagelibrary.http.dto.UserDto
import spray.http.StatusCodes
import spray.routing.{Route, HttpService}
import scala.concurrent.ExecutionContext.Implicits.global
import com.github.simonthecat.imagelibrary.http.route.JsonImplicits._
import spray.httpx.SprayJsonSupport._
import spray.httpx.marshalling.MetaToResponseMarshallers._

trait UserRoutesService extends HttpService with ExtraDirectives {

  def userStorage: UserStorage

  def getUser(implicit user: User): Route =
    path("user" / Segment) { username =>
      get {
        if (user.username == username) {
          complete {
            userStorage.getUser(username).map(_.map(UserDto.fromStoredUser))
          }
        } else {
          completeWithJsonError(StatusCodes.Forbidden, "You can access only your own user via REST api")
        }
      }
    }

}
