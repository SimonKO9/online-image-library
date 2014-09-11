package com.github.simonthecat.imagelibrary.core.security

case class StoredUser(username: String, passwordHash: String, salt: String)