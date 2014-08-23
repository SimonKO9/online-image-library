package com.github.simonthecat.imagelibrary.route

import spray.http.MultipartFormData
import spray.routing._
import spray.routing.directives.DetachMagnet

import scala.concurrent.ExecutionContext.Implicits.global

trait FileUploadRoutes extends HttpService {
  def log: akka.event.LoggingAdapter

  val uploadImage: Route =
    path("api" / "images") {
      post {
        entity(as[MultipartFormData]) { formData =>
          detach(new DetachMagnet()) {
            processFormData(formData)
          }
        }
      }
    }

  val getImage: Route =
    pathPrefix("api" / "images") {
      path(Segment) { imageId =>
        returnImage(imageId)
      }
    }

  def returnImage(imageId: String): Route

  def processFormData(formData: MultipartFormData): Route
}
