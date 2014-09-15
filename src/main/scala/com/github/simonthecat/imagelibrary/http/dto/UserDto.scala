package com.github.simonthecat.imagelibrary.http.dto

import com.github.simonthecat.imagelibrary.core.security.StoredUser

case class UserDto(username: String)

object UserDto {
  implicit def fromStoredUser(storedUser: StoredUser) = UserDto(storedUser.username)
}
