package com.github.simonthecat.imagelibrary.core.storage

import reactivemongo.bson._

case class ImageDocument(_id: BSONObjectID = BSONObjectID.generate, fileName: String, data: BSONBinary) {
  def dataArray = data.value.readArray(data.value.size)

  def toDocument: BSONDocument = ImageDocument.imageDocHandler.write(this)
}

object ImageDocument {
  implicit val imageDocHandler = Macros.handler[ImageDocument]

  def apply(fileName: String, bytes: Array[Byte]): ImageDocument =
    ImageDocument(fileName = fileName, data = BSONBinary(bytes, Subtype.UserDefinedSubtype))
}