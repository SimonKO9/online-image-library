package com.github.simonthecat.imagelibrary.http.route.api

import com.github.simonthecat.imagelibrary.core.security.{Security, StoredUser, UserStorage}
import com.github.simonthecat.imagelibrary.http.auth.User
import com.github.simonthecat.imagelibrary.http.dto.{UserDto, UserRegistrationDataDto}
import com.github.simonthecat.imagelibrary.http.route.ExtraDirectives
import com.github.simonthecat.imagelibrary.http.route.JsonImplicits._
import spray.http.StatusCodes
import spray.httpx.SprayJsonSupport._
import spray.httpx.marshalling.MetaToResponseMarshallers._
import spray.routing.{HttpService, Route}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait UserRoutesService extends HttpService with ExtraDirectives {

  def userStorage: UserStorage

  def security: Security

  def getUser(implicit user: User): Route =
    path("user" / Segment) { username =>
      get {
        if (user.username == username) {
          complete {
            userStorage.getUser(username).map(optUserToDto) // TODO extract UserService in core
          }
        } else {
          completeWithJsonError(StatusCodes.Forbidden, "You can access only your own user via REST api")
        }
      }
    }

  def createUser: Route =
    path("user") {
      post {
        respondWithStatus(StatusCodes.Created) {
          entity(as[UserRegistrationDataDto]) { data =>
            complete {
              doRegisterUser(data).map(optUserToDto) // TODO extract UserService in core
            }
          }
        }
      }
    }

  def doRegisterUser(data: UserRegistrationDataDto): Future[Option[StoredUser]] = {
    userStorage.getUser(data.username).flatMap {
      case Some(existingUser) =>
        Future.successful(None)
      case None =>
        val salt = "123"
        val createUser = StoredUser(data.username, security.hash(salt, data.password), salt)
        userStorage.createUser(createUser)
    }
  }

  def optUserToDto(optStoredUser: Option[StoredUser]) = optStoredUser.map(UserDto.fromStoredUser)
}
