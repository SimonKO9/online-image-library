package com.github.simonthecat.imagelibrary.http.route

import spray.http._
import spray.json._
import spray.routing._
import spray.routing.directives.DetachMagnet
import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global

trait ExtraDirectives {
  this: HttpService =>

  def completeWithStatusCodeAsJson(statusCode: StatusCode, json: JsValue): Route =
    respondWithStatus(statusCode) {
      respondWithMediaType(MediaTypes.`application/json`) {
        complete(json.toString())
      }
    }

  def completeWithJsonError(statusCode: StatusCode, msg: String): Route =
    completeWithStatusCodeAsJson(statusCode, JsObject(("reason", JsString(msg))))

  def extractMediaType: PartialFunction[HttpHeader, MediaType] = {
    case contentType: HttpHeaders.`Content-Type` =>
      contentType.contentType.mediaType
  }

  def withMultipart(partName: String)(f: Option[BodyPart] => (Route)) =
    headerValuePF(extractMediaType) {
      case _: MultipartMediaType =>
        entity(as[MultipartFormData]) { formData =>
          detach(new DetachMagnet()) {
            f(formData.get("uploadedFile"))
          }
        }
      case _ =>
        reject(UnsupportedRequestContentTypeRejection("Invalid Content-Type."))
    }
}
