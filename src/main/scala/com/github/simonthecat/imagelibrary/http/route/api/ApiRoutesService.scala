package com.github.simonthecat.imagelibrary.http.route.api

import com.github.simonthecat.imagelibrary.http.route.BasicAuthDirectives

trait ApiRoutesService extends ImageRoutesService with BasicAuthDirectives {

  private val securedRoutes = auth { implicit user =>
    uploadImageRoute
  }

  private val publicRoutes = getImageRoute

  val apiRoutes = pathPrefix("api") {
    publicRoutes ~ securedRoutes
  }

}
