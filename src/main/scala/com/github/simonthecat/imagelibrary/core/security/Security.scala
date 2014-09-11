package com.github.simonthecat.imagelibrary.core.security

import scala.concurrent.Future

trait Security {

  def login(username: String, password: String): Future[Option[StoredUser]]

  def hash(salt: String, password: String): String

}
