package com.github.simonthecat.imagelibrary

import java.io.FileOutputStream
import java.nio.file.{Files, Paths}

import akka.actor.{Actor, ActorLogging, ActorRefFactory}
import com.github.simonthecat.imagelibrary.route.{FileUploadRoutes, StaticResourcesRoutes}
import spray.http.MediaTypes._
import spray.http.{HttpHeaders, MultipartFormData, StatusCodes}
import spray.json._
import spray.routing._

class RouteServiceActor extends Actor with ActorLogging with HttpService
with StaticResourcesRoutes with FileUploadRoutes {

  implicit val system = context.system

  override implicit def actorRefFactory: ActorRefFactory = context

  override def receive: Receive = runRoute(myRoute)

  val myRoute = {
    val websiteRoutes = rootIndexHtml ~ staticFiles
    val apiRoutes = uploadImage ~ getImage

    websiteRoutes ~ apiRoutes
  }

  override def processFormData(formData: MultipartFormData): Route = respondWithMediaType(`application/json`) {
    formData.get("uploadedFile") match {
      case Some(part) =>
        val byteBuffer = part.entity.data.toByteString.asByteBuffer
        val contentDisposation = part.headers.find(_.is("content-disposition")).get.value
        val originalFileName = contentDisposation.split("filename=").last
        new FileOutputStream(s"/home/simon/tmp/_uploads/$originalFileName", false).getChannel.write(byteBuffer)
        respondWithHeader(HttpHeaders.Location(s"/api/images/$originalFileName")) {
          complete(s"OK")
        }
      case None =>
        respondWithStatus(StatusCodes.BadRequest) {
          complete("Missing or invalid file")
        }
    }
  }

  override def returnImage(imageId: String): Route = {
    val filePath = s"/home/simon/tmp/_uploads/$imageId"
    if (Files.exists(Paths.get(filePath))) {
      getFromFile(filePath)
    } else {
      respondWithMediaType(`application/json`) {
        respondWithStatus(StatusCodes.NotFound) {
          val errorMsg = JsObject(("reason", JsString("resource does not exist"))).toString()
          complete(errorMsg)
        }
      }
    }
  }
}
