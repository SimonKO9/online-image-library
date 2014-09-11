package com.github.simonthecat.imagelibrary.core.security

import com.github.simonthecat.imagelibrary.http.auth.User

import scala.concurrent.Future

trait UserStorage {

  def getUser(username: String): Future[Option[StoredUser]]

}
