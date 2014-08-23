package com.github.simonthecat.imagelibrary.route

import spray.routing._
import spray.routing.directives.CachingDirectives._

trait StaticResourcesRoutes extends HttpService {
  val rootIndexHtml = {
    val serveGet = get {
      getFromResource("private/index.html")
    }

    path("index.html") {
      serveGet
    } ~ path("") {
      serveGet
    }
  }

  val staticFiles: Route = pathPrefix("public") {
    get {
      cache(routeCache()) {
        getFromResourceDirectory("public")
      }
    }
  } ~ pathPrefix("webjars") {
    get {
      cache(routeCache()) {
        getFromResourceDirectory("META-INF/resources/webjars")
      }
    }
  }

}
