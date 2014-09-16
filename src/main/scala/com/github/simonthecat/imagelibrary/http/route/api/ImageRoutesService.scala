package com.github.simonthecat.imagelibrary.http.route.api

import java.io.{ByteArrayInputStream, InputStream}

import com.github.simonthecat.imagelibrary.core.storage._
import com.github.simonthecat.imagelibrary.http.auth.User
import com.github.simonthecat.imagelibrary.http.route.ExtraDirectives
import com.google.common.io.ByteStreams
import spray.http._
import spray.json.JsString
import spray.routing._
import spray.routing.directives._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

protected trait ImageRoutesService extends HttpService with ExtraDirectives {

  def imageStorage: ImageStorage

  def uploadImageRoute(implicit user: User): Route =
    path("images") {
      post {
        withMultipart("uploadedFile") {
          case Some(part) =>
            val byteArray = part.entity.data.toByteArray
            val byteInputStream = new ByteArrayInputStream(byteArray)
            val contentDisposation = part.headers.find(_.is("content-disposition")).get.value
            val originalFileName = contentDisposation.split("filename=").last
            handleImageUpload(originalFileName, byteInputStream)
          case None =>
            completeWithJsonError(StatusCodes.BadRequest, "Missing data part")
        } ~
          completeWithJsonError(StatusCodes.BadRequest, "Invalid request: expected multipart/form-data")
      }
    }

  def getImageRoute: Route =
    path("images" / Segment) { imageId =>
        onComplete(resolveImage(imageId)) {
          case Success(Some((fileName, bytes))) =>
            val httpEntity = HttpEntity(ContentTypeResolver.Default(fileName), HttpData(bytes))
            complete(httpEntity)
          case Success(None) =>
            completeWithJsonError(StatusCodes.NotFound, "Resource not found")
          case Failure(ex) =>
            completeWithJsonError(StatusCodes.InternalServerError, ex.getMessage)
        }
    }
  
  private def handleImageUpload(fileName: String, bytes: InputStream) =
    onComplete(imageStorage.save(fileName, bytes, None)) { // TODO
      case Success(ImageStoreSuccess(imageId)) =>
        respondWithHeader(HttpHeaders.Location(s"/api/images/$imageId")) {
          complete(JsString("OK").toString())
        }
      case Success(ImageStoreError(reason)) =>
        completeWithJsonError(StatusCodes.BadRequest, reason)
      case Failure(ex) =>
        completeWithJsonError(StatusCodes.InternalServerError, ex.getMessage)
    }

  private def resolveImage(imageId: String)(implicit resolver: ContentTypeResolver): Future[Option[(String, Array[Byte])]] =
    imageStorage.get(imageId) map {
      case Some(img) =>
        val bytes = ByteStreams.toByteArray(img.bytes)
        Some((img.fileName, bytes))
      case None =>
        None
    }

}
