package com.github.simonthecat.imagelibrary.http.route.api

import com.github.simonthecat.imagelibrary.http.route.BasicAuthDirectives

trait ApiRoutesService extends BasicAuthDirectives with ImageRoutesService with UserRoutesService {

  private val securedRoutes = auth { implicit user =>
    uploadImageRoute ~ getUser
  }

  private val publicRoutes = getImageRoute

  val apiRoutes = pathPrefix("api") {
    publicRoutes ~ securedRoutes
  }

}
