package com.github.simonthecat.imagelibrary.core.storage

import java.io.InputStream
import java.nio.file.{Files, Paths}

import scala.concurrent.{ExecutionContext, Future}

class FileImageStorage(storePath: String)(implicit val ec: ExecutionContext) extends ImageStorage {

  override def save(fileName: String, bytes: InputStream): Future[ImageStoreResult] =
    Future {
      val path = Paths.get(s"$storePath$fileName")
      if (Files.exists(path)) {
        ImageStoreError("Resource already exists")
      } else {
        Files.copy(bytes, path)
        ImageStoreSuccess(fileName)
      }
    }

  override def get(imageId: String): Future[Option[StoredImage]] =
    Future {
      val path = Paths.get(s"$storePath$imageId")
      if (Files.exists(path)) {
        Some(StoredImage(Files.newInputStream(path), imageId))
      } else {
        None
      }
    }
}
