package com.github.simonthecat.imagelibrary.core.storage

import java.io.InputStream

case class StoredImage(bytes: InputStream, fileName: String, owner: Option[ImageOwner] = None)

case class ImageOwner(uid: String, username: String)