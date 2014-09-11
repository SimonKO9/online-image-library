package com.github.simonthecat.imagelibrary

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import com.github.simonthecat.imagelibrary.core.{MongoModule, AppModule}
import com.github.simonthecat.imagelibrary.http.RouteServiceActor
import org.apache.logging.log4j.LogManager
import spray.can.Http

object Boot extends App with AppModule with MongoModule {

  val log = LogManager.getLogger(this.getClass)
  log.info("Starting up application...")
  log.debug("test")

  implicit val routeService = system.actorOf(RouteServiceActor.props, "routeServiceActor")

  IO(Http) ! Http.Bind(listener = routeService, port = 8080, interface = "localhost")

}
