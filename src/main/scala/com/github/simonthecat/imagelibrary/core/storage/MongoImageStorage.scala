package com.github.simonthecat.imagelibrary.core.storage

import java.io.{ByteArrayInputStream, InputStream}

import com.github.simonthecat.imagelibrary.core.storage.ImageDocument._
import com.google.common.io.ByteStreams
import reactivemongo.api._
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class MongoImageStorage(implicit val db: DB, implicit val ec: ExecutionContext) extends ImageStorage {

  val collection: BSONCollection = db.collection("images")


  override def save(fileName: String, bytes: InputStream): Future[ImageStoreResult] = {
    val byteArray = ByteStreams.toByteArray(bytes)
    val document = ImageDocument(fileName, byteArray)

    collection.insert(document).map {
      error =>
        if (!error.ok) ImageStoreError(error.message)
        else ImageStoreSuccess(document._id.stringify)
    }
  }

  override def get(imageId: String): Future[Option[StoredImage]] = {
    BSONObjectID.parse(imageId) match {
      case Success(bsonObjectId) =>
        val query = BSONDocument("_id" -> BSONObjectID(imageId))
        collection.find(query).cursor[ImageDocument].collect[List]().map(_.headOption).map {
          case Some(document) =>
            val byteInputStream = new ByteArrayInputStream(document.dataArray)
            val img = StoredImage(byteInputStream, document.fileName)
            Some(img)
          case None =>
            None
        }
      case Failure(_) =>
        Future.successful(None)
    }

  }
}
