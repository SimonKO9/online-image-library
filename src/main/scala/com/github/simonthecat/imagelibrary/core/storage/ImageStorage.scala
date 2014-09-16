package com.github.simonthecat.imagelibrary.core.storage

import java.io.InputStream

import com.github.simonthecat.imagelibrary.core.security.StoredUser
import com.github.simonthecat.imagelibrary.http.auth.User

import scala.concurrent.Future

trait ImageStorage {

  def save(fileName: String, bytes: InputStream, userOpt: Option[StoredUser]): Future[ImageStoreResult]

  def get(imageId: String): Future[Option[StoredImage]]

}
