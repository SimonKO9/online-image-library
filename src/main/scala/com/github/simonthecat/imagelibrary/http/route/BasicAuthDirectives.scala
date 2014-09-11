package com.github.simonthecat.imagelibrary.http.route

import com.github.simonthecat.imagelibrary.http.auth.User
import spray.routing._
import spray.routing.authentication.{BasicAuth, UserPassAuthenticator}
import spray.routing.directives.AuthMagnet

import scala.concurrent.ExecutionContext.Implicits.global

trait BasicAuthDirectives {
  this: HttpService =>

  def authenticator: UserPassAuthenticator[User]

  def authMagnet: AuthMagnet[User] =
    AuthMagnet.fromContextAuthenticator(BasicAuth(authenticator, "Image Library"))

  def auth = authenticate(authMagnet)

}
