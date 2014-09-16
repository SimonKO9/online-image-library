package com.github.simonthecat.imagelibrary.core.storage

import com.github.simonthecat.imagelibrary.core.security.StoredUser
import reactivemongo.bson._

case class ImageDocument(_id: BSONObjectID = BSONObjectID.generate, fileName: String, data: BSONBinary, owner: Option[ImageOwnerDocument] = None) {
  def dataArray = data.value.readArray(data.value.size)

  def toDocument: BSONDocument = ImageDocument.imageDocHandler.write(this)
}

case class ImageOwnerDocument(uid: String, username: String)

object ImageDocument {

  implicit val imageOwnerHandler = Macros.handler[ImageOwnerDocument]

  implicit val imageDocHandler = Macros.handler[ImageDocument]


  def apply(fileName: String, bytes: Array[Byte], userOpt: Option[StoredUser]): ImageDocument = {
    val ownerOpt = userOpt.map(u => ImageOwnerDocument(s"ID:${u.username}", u.username))
    ImageDocument(fileName = fileName, data = BSONBinary(bytes, Subtype.UserDefinedSubtype), owner = ownerOpt)
  }
}