package com.github.simonthecat.imagelibrary.core

import akka.actor.ActorSystem
import com.github.simonthecat.imagelibrary.core.storage.ImageStorage
import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContext

trait AppModule extends MongoModule {

  val cfg = ConfigFactory.load()

  implicit val system = ActorSystem("image-library")

  implicit val ec: ExecutionContext = system.dispatcher

//  implicit val imageStorage: ImageStorage = new FileImageStorage(cfg.getString("image-library.store-path"))
  implicit val imageStorage: ImageStorage
}
