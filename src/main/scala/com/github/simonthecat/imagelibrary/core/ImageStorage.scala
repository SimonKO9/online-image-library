package com.github.simonthecat.imagelibrary.core

import java.io.InputStream

trait ImageStorage {

  def save(fileName: String, bytes: InputStream): ImageStoreResult

  def get(imageId: String): Option[StoredImage]

}
