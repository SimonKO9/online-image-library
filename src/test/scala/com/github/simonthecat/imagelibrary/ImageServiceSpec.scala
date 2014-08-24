package com.github.simonthecat.imagelibrary

import java.io.ByteArrayInputStream

import akka.actor.ActorRefFactory
import com.github.simonthecat.imagelibrary.core.{ImageStorage, ImageStoreSuccess, StoredImage}
import com.github.simonthecat.imagelibrary.http.route.ImageService
import org.mockito.Matchers
import org.mockito.Mockito._
import org.specs2.mutable.Specification
import spray.http._
import spray.json._
import spray.testkit.Specs2RouteTest

class ImageServiceSpec extends Specification with Specs2RouteTest {

  case class TestImageService(imageStorage: ImageStorage, actorRefFactory: ActorRefFactory = system) extends ImageService

  "ImageService" should {
    "return '200 OK' with image payload for existing image ID" in {
      val imageStorage = mock(classOf[ImageStorage])
      val img = StoredImage(new ByteArrayInputStream("dummy".getBytes("UTF-8")), "my-image.jpg")
      when(imageStorage.get("my-image.jpg")).thenReturn(Some(img))
      val service = new TestImageService(imageStorage)

      Get("/api/images/my-image.jpg") ~> service.getImage ~> check {
        status === StatusCodes.OK
        contentType === ContentType(MediaTypes.`image/jpeg`)
      }
    }

    "return '404 Not Found' with json payload for nonexistent image" in {
      val imageStorage = mock(classOf[ImageStorage])
      when(imageStorage.get(Matchers.anyString())).thenReturn(None)
      val service = new TestImageService(imageStorage)

      Get("/api/images/non-existent-image.jpg") ~> service.getImage ~> check {
        status === StatusCodes.NotFound
        contentType === ContentType(MediaTypes.`application/json`, HttpCharsets.`UTF-8`)
        responseAs[String].parseJson === JsObject(("reason", JsString("Resource not found")))
      }
    }

    "return '400 Bad Request' with json payload for POST request with invalid Content-Type" in {
      val imageStorage = mock(classOf[ImageStorage])
      when(imageStorage.get(Matchers.anyString())).thenReturn(None)
      val service = new TestImageService(imageStorage)

      val request = Post("/api/images").withHeaders(HttpHeaders.`Content-Type`(ContentTypes.`application/json`))
      request ~> service.uploadImage ~> check {
        status === StatusCodes.BadRequest
        responseAs[String].parseJson === JsObject(("reason", JsString("Invalid request: expected multipart/form-data")))
      }
    }

    "return '400 Bad Request' with json payload for POST request with missing data part" in {
      val imageStorage = mock(classOf[ImageStorage])
      when(imageStorage.get(Matchers.anyString())).thenReturn(None)
      val service = new TestImageService(imageStorage)

      val request = Post("/api/images").withHeaders(HttpHeaders.`Content-Type`(ContentType(MediaTypes.`multipart/form-data`)))
      request ~> service.uploadImage ~> check {
        status === StatusCodes.BadRequest
        responseAs[String].parseJson === JsObject(("reason", JsString("Missing data part")))
      }
    }

    "return '200 OK' with json payload for POST request with image payload" in {
      val imageStorage = mock(classOf[ImageStorage])
      val img = StoredImage(new ByteArrayInputStream("dummy".getBytes("UTF-8")), "my-image.jpg")
      when(imageStorage.save(Matchers.anyString(), Matchers.anyObject())).thenReturn(ImageStoreSuccess("my-image.jpg"))

      val service = new TestImageService(imageStorage)
      val entity = HttpEntity(ContentType(MediaTypes.`image/jpeg`), "dummy".getBytes("UTF-8"))
      val formData = MultipartFormData(List(BodyPart(entity, "uploadedFile")))

      val request = Post("/api/images", formData).withHeaders(HttpHeaders.`Content-Type`(ContentType(MediaTypes.`multipart/form-data`)))

      request ~> service.uploadImage ~> check {
        responseAs[String].parseJson === JsString("OK")
        status === StatusCodes.OK
      }
    }

  }
}
