package com.github.simonthecat.imagelibrary

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import com.github.simonthecat.imagelibrary.core.AppModule
import com.github.simonthecat.imagelibrary.http.RouteServiceActor
import spray.can.Http

object Boot extends App with AppModule {
  implicit val system = ActorSystem("image-library")

  implicit val routeService = system.actorOf(RouteServiceActor.props, "routeServiceActor")

  IO(Http) ! Http.Bind(listener = routeService, port = 8080, interface = "localhost")
}
