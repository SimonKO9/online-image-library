package com.github.simonthecat.imagelibrary.http.route

import java.io.{ByteArrayInputStream, InputStream}

import com.github.simonthecat.imagelibrary.core.storage._
import spray.http.MediaTypes._
import spray.http._
import spray.json.{JsObject, JsString}
import spray.routing._
import spray.routing.directives.{ContentTypeResolver, DetachMagnet}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

trait ImageService extends HttpService {

  def imageStorage: ImageStorage

  def extractMediaType: PartialFunction[HttpHeader, MediaType] = {
    case contentType: HttpHeaders.`Content-Type` =>
      contentType.contentType.mediaType
  }

  val uploadImage: Route =
    path("api" / "images") {
      post {
        headerValuePF(extractMediaType) {
          case _: MultipartMediaType =>
            entity(as[MultipartFormData]) { formData =>
              detach(new DetachMagnet()) {
                respondWithMediaType(`application/json`) {
                  formData.get("uploadedFile") match {
                    case Some(part) =>
                      val byteArray = part.entity.data.toByteArray
                      val byteInputStream = new ByteArrayInputStream(byteArray)
                      val contentDisposation = part.headers.find(_.is("content-disposition")).get.value
                      val originalFileName = contentDisposation.split("filename=").last
                      handleImageUpload(originalFileName, byteInputStream)
                    case None =>
                      respondWithStatus(StatusCodes.BadRequest) {
                        val json = JsObject(("reason", JsString("Missing data part")))
                        complete(json.toString())
                      }
                  }
                }
              }
            }
          case other =>
            respondWithStatus(StatusCodes.BadRequest) {
              val json = JsObject(("reason", JsString(s"Invalid request: expected multipart/form-data")))
              complete(json.toString())
            }
        }
      }
    }

  val getImage: Route =
    pathPrefix("api" / "images") {
      path(Segment) { imageId =>
        resolveImage(imageId)
      }
    }


  private def resolveImage(imageId: String)(implicit resolver: ContentTypeResolver) = {
    onComplete(imageStorage.get(imageId)) {
      case Success(value) =>
        value match {
          case Some(img) =>
            detach(new DetachMagnet()) {
              val bytes = Stream.continually(img.bytes.read).takeWhile(_ != -1).map(_.toByte).toArray
              val httpEntity = HttpEntity(resolver(img.fileName), HttpData(bytes))
              complete(httpEntity)
            }
          case None =>
            respondWithStatus(StatusCodes.NotFound) {
              respondWithMediaType(MediaTypes.`application/json`) {
                val json = JsObject(("reason", JsString("Resource not found")))
                complete(json.toString())
              }
            }
        }
      case Failure(ex) =>
        respondWithStatus(StatusCodes.InternalServerError) {
          complete(ex.getMessage)
        }
    }
  }

  private def handleImageUpload(fileName: String, bytes: InputStream) = onComplete(imageStorage.save(fileName, bytes)) {
    case Success(value) =>
      value match {
        case ImageStoreSuccess(imageId) =>
          respondWithHeader(HttpHeaders.Location(s"/api/images/$imageId")) {
            complete(JsString("OK").toString())
          }
        case ImageStoreError(reason) =>
          respondWithStatus(StatusCodes.BadRequest) {
            val json = JsObject(("reason", JsString(reason)))
            complete(json.toString())
          }
      }
    case Failure(ex) =>
      respondWithStatus(StatusCodes.InternalServerError) {
        complete(ex.getMessage)
      }
  }
}
