package com.github.simonthecat.imagelibrary.core.security

import scala.concurrent.Future

trait UserStorage {

  def getUser(username: String): Future[Option[StoredUser]]

  def createUser(user: StoredUser): Future[Option[StoredUser]]

}
