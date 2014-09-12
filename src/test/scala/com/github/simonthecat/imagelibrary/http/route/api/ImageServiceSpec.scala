package com.github.simonthecat.imagelibrary.http.route.api

import java.io.ByteArrayInputStream

import akka.actor.ActorRefFactory
import com.github.simonthecat.imagelibrary.core.storage.{ImageStorage, ImageStoreSuccess, StoredImage}
import com.github.simonthecat.imagelibrary.http.auth.User
import org.mockito.Matchers
import org.mockito.Mockito._
import org.specs2.mutable.Specification
import spray.http._
import spray.json._
import spray.testkit.Specs2RouteTest

import scala.concurrent.{ExecutionContext, Future}

class ImageServiceSpec extends Specification with Specs2RouteTest {

  implicit val ec: ExecutionContext = system.dispatcher

  implicit val user = User("testUser")

  case class TestImageService(imageStorage: ImageStorage, actorRefFactory: ActorRefFactory = system) extends ImageRoutesService

  "ImageService" should {
    "return '200 OK' with image payload for existing image ID" in {
      val imageStorage = mock(classOf[ImageStorage])
      val img = StoredImage(new ByteArrayInputStream("dummy".getBytes("UTF-8")), "my-image.jpg")

      when(imageStorage.get("my-image.jpg")).thenReturn(Future.successful(Some(img)))
      val service = new TestImageService(imageStorage)

      Get("/images/my-image.jpg") ~> service.getImageRoute ~> check {
        status === StatusCodes.OK
        contentType === ContentType(MediaTypes.`image/jpeg`)
      }
    }

    "return '404 Not Found' with json payload for nonexistent image" in {
      val imageStorage = mock(classOf[ImageStorage])
      when(imageStorage.get(Matchers.anyString())).thenReturn(Future.successful(None))
      val service = new TestImageService(imageStorage)

      Get("/images/non-existent-image.jpg") ~> service.getImageRoute ~> check {
        status === StatusCodes.NotFound
        contentType === ContentType(MediaTypes.`application/json`, HttpCharsets.`UTF-8`)
        responseAs[String].parseJson === JsObject(("reason", JsString("Resource not found")))
      }
    }

    "return '400 Bad Request' with json payload for POST request with invalid Content-Type" in {
      val imageStorage = mock(classOf[ImageStorage])
      when(imageStorage.get(Matchers.anyString())).thenReturn(Future.successful(None))
      val service = new TestImageService(imageStorage)

      val request = Post("/images").withHeaders(HttpHeaders.`Content-Type`(ContentTypes.`application/json`))
      request ~> service.uploadImageRoute ~> check {
        status === StatusCodes.BadRequest
        responseAs[String].parseJson === JsObject(("reason", JsString("Invalid request: expected multipart/form-data")))
      }
    }

    "return '400 Bad Request' with json payload for POST request with missing data part" in {
      val imageStorage = mock(classOf[ImageStorage])
      when(imageStorage.get(Matchers.anyString())).thenReturn(Future.successful(None))
      val service = new TestImageService(imageStorage)

      val request = Post("/images").withHeaders(HttpHeaders.`Content-Type`(ContentType(MediaTypes.`multipart/form-data`)))
      request ~> service.uploadImageRoute ~> check {
        status === StatusCodes.BadRequest
        responseAs[String].parseJson === JsObject(("reason", JsString("Missing data part")))
      }
    }

    "return '200 OK' with json payload for POST request with image payload" in {
      val imageStorage = mock(classOf[ImageStorage])
      val img = StoredImage(new ByteArrayInputStream("dummy".getBytes("UTF-8")), "my-image.jpg")
      when(imageStorage.save(Matchers.anyString(), Matchers.anyObject())).thenReturn(Future.successful(ImageStoreSuccess("my-image.jpg")))

      val service = new TestImageService(imageStorage)
      val entity = HttpEntity(ContentType(MediaTypes.`image/jpeg`), "dummy".getBytes("UTF-8"))
      val formData = MultipartFormData(List(BodyPart(entity, "uploadedFile")))

      val request = Post("/images", formData).withHeaders(HttpHeaders.`Content-Type`(ContentType(MediaTypes.`multipart/form-data`)))

      request ~> service.uploadImageRoute ~> check {
        responseAs[String].parseJson === JsString("OK")
        status === StatusCodes.OK
      }
    }

  }
}
