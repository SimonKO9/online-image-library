package com.github.simonthecat.imagelibrary

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http

object Boot extends App {
  implicit val system = ActorSystem("image-library")

  implicit val routeService = system.actorOf(Props(classOf[RouteServiceActor]), "route-service")

  IO(Http) ! Http.Bind(listener = routeService, port = 8080, interface = "localhost")
}
