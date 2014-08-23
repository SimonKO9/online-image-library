package com.github.simonthecat.imagelibrary.core

import java.io.InputStream
import java.nio.file.{Files, Paths}

class FileImageStorage(storePath: String) extends ImageStorage {

  override def save(fileName: String, bytes: InputStream): ImageStoreResult = {
    val path = Paths.get(s"$storePath$fileName")
    if (Files.exists(path)) {
      ImageStoreError("Resource already exists")
    } else {
      Files.copy(bytes, path)
      ImageStoreSuccess(fileName)
    }

  }

  override def get(imageId: String): Option[StoredImage] = {
    val path = Paths.get(s"$storePath$imageId")
    if(Files.exists(path)) {
      Some(StoredImage(Files.newInputStream(path), imageId))
    } else {
      None
    }
  }
}
