package com.github.simonthecat.imagelibrary.core

import com.github.simonthecat.imagelibrary.core.storage.{ImageStorage, MongoImageStorage}
import reactivemongo.api.MongoDriver

trait MongoModule {
  this: AppModule =>

  implicit lazy val mongo = new MongoDriver(system)

  implicit lazy val connection = mongo.connection(List("localhost"))

  implicit lazy val db = connection.db("imagelibrary")

  implicit lazy val imageStorage: ImageStorage = new MongoImageStorage()
}
