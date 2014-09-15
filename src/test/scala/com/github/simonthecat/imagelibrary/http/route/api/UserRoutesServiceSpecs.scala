package com.github.simonthecat.imagelibrary.http.route.api

import akka.actor.ActorRefFactory
import com.github.simonthecat.imagelibrary.core.security.{StoredUser, UserStorage}
import com.github.simonthecat.imagelibrary.http.auth.User
import com.github.simonthecat.imagelibrary.http.dto.UserDto
import org.specs2.mutable._
import spray.http.StatusCodes
import spray.json.{JsString, JsObject}
import spray.testkit.Specs2RouteTest
import org.mockito.Mockito._
import com.github.simonthecat.imagelibrary.http.route.JsonImplicits._
import spray.httpx.SprayJsonSupport._
import spray.json._


import scala.concurrent.Future

class UserRoutesServiceSpecs extends Specification with Specs2RouteTest {
  
  case class TestUserRoutesService(userStorage: UserStorage, actorRefFactory: ActorRefFactory = system) extends UserRoutesService
  
  "User routes" should {
    "return 200 OK and user entity if logged in user is equal to requested" in {
      val user = User("testuser")
      val userStorage = mock(classOf[UserStorage])
      when(userStorage.getUser(user.username)).thenReturn(Future.successful(Some(StoredUser(user.username, "hash", "salt"))))
      val service = TestUserRoutesService(userStorage)

      Get(s"/user/${user.username}") ~> service.getUser(user) ~> check {
        responseAs[UserDto] === UserDto(user.username)
        status === StatusCodes.OK
      }
    }

    "return 403 Forbidden and error message if logged in user is different from requested" in {
      val loggedInUser = User("testuser")
      val requestedUser = "requestedUser"

      val userStorage = mock(classOf[UserStorage])
      when(userStorage.getUser(loggedInUser.username)).thenReturn(Future.successful(Some(StoredUser(loggedInUser.username, "hash", "salt"))))
      val service = TestUserRoutesService(userStorage)

      Get(s"/user/$requestedUser") ~> service.getUser(loggedInUser) ~> check {
        status === StatusCodes.Forbidden
        responseAs[String].parseJson === JsObject(("reason", JsString("You can access only your own user via REST api")))
      }
    }
  }


  
}
