package com.github.simonthecat.imagelibrary.http

import akka.actor.{Actor, ActorLogging, ActorRefFactory, Props}
import com.github.simonthecat.imagelibrary.core.storage.ImageStorage
import com.github.simonthecat.imagelibrary.http.route.{ImageService, StaticResourcesRoutes}
import spray.routing._

class RouteServiceActor(val imageStorage: ImageStorage) extends Actor with ActorLogging with HttpService
with StaticResourcesRoutes with ImageService {

  implicit val system = context.system

  override implicit def actorRefFactory: ActorRefFactory = context

  override def receive: Receive = runRoute(myRoute)

  val myRoute = {
    val websiteRoutes = rootIndexHtml ~ staticFiles
    val apiRoutes = uploadImage ~ getImage

    websiteRoutes ~ apiRoutes
  }

}

object RouteServiceActor {
  def props(implicit imageStorage: ImageStorage): Props = Props(classOf[RouteServiceActor], imageStorage)
}