package com.github.simonthecat.imagelibrary.http.route

import com.github.simonthecat.imagelibrary.http.dto.UserDto
import spray.json.DefaultJsonProtocol._

object JsonImplicits {

  implicit val userFormat = jsonFormat1(UserDto.apply)

}
