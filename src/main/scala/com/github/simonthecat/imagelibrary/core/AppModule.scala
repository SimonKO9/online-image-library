package com.github.simonthecat.imagelibrary.core

import akka.actor.ActorSystem
import com.github.simonthecat.imagelibrary.core.security.{DefaultSecurity, Security, UserStorage}
import com.github.simonthecat.imagelibrary.core.storage.ImageStorage
import com.github.simonthecat.imagelibrary.http.auth.{User, UserPassAuth}
import com.typesafe.config.ConfigFactory
import spray.routing.authentication.UserPassAuthenticator

import scala.concurrent.ExecutionContext

trait AppModule {

  val cfg = ConfigFactory.load()

  implicit val system = ActorSystem("image-library")

  implicit val ec: ExecutionContext = system.dispatcher

  implicit val security: Security = new DefaultSecurity()

  implicit val authenticator: UserPassAuthenticator[User] = new UserPassAuth().login

  implicit val imageStorage: ImageStorage

  implicit val userStorage: UserStorage

}
