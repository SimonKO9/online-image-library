package com.github.simonthecat.imagelibrary.core.storage

import java.io.InputStream

import scala.concurrent.Future

trait ImageStorage {

  def save(fileName: String, bytes: InputStream): Future[ImageStoreResult]

  def get(imageId: String): Future[Option[StoredImage]]

}
