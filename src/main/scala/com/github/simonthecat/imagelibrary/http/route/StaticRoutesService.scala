package com.github.simonthecat.imagelibrary.http.route

import spray.routing._
import spray.routing.directives.CachingDirectives._

trait StaticRoutesService extends HttpService {

  private val rootIndexHtml =
    (path("index.html") | path("")) {
      getFromResource("private/index.html")
    }


  private val staticFiles: Route = pathPrefix("public") {
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

  val staticRoutes = rootIndexHtml ~ staticFiles

}
