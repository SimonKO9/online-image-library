package com.github.simonthecat.imagelibrary.core

import com.typesafe.config.ConfigFactory

trait AppModule {

  val cfg = ConfigFactory.load()

  implicit val imageStorage: ImageStorage = new FileImageStorage(cfg.getString("image-library.store-path"))

}
