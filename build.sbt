import spray.revolver.RevolverPlugin._

organization := "com.example"

version := "0.1"

scalaVersion := "2.11.2"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8", "-target:jvm-1.7")

resolvers += "spray repo" at "http://repo.spray.io"

resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= {
  val akkaV = "2.3.5"
  val sprayV = "1.3.1"
  Seq(
    "io.spray" %% "spray-can" % sprayV,
    "io.spray" %% "spray-routing" % sprayV,
    "io.spray" %% "spray-caching" % sprayV,
    "io.spray" %% "spray-json" % "1.2.6",
    "io.spray" %% "spray-testkit" % sprayV % "test",
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-testkit" % akkaV % "test",
    "org.specs2" %% "specs2" % "2.4.1" % "test",
    "org.webjars" % "bootstrap" % "3.2.0",
    "org.webjars" % "jquery" % "2.1.1",
    "org.reactivemongo" %% "reactivemongo" % "0.11.0-SNAPSHOT",
    "com.google.guava" % "guava" % "18.0"
  )
}

Revolver.settings

