Online image library
====================

Simplified version of popular image-hosting website ImageShack. Code was developed for educational purposes. :)

I've tried to follow best guidelines found in spray and specs2 docs, analyze googlegroups conversations related to those libs. If some question remained unanswered to me, I've digged into stackoverflow. 


What's used
===========
* akka - everything is built on top of it
* spray stack
    * spray-can for standalone http server
    * spray-routing for nice dsl for routing
    * spray-testkit for testing spray routing
    * spray-json for json
    * spray-caching for... caching :)
* webjars as a convenient way of managing client-side libraries
* specs2 for testing
