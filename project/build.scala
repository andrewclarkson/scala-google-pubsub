import sbt._
import Keys._


object build extends Build {

  val baseSettings = Seq(
    organization := "mn.clarkson",
    scalaVersion := "2.11.8",
    libraryDependencies ++= Seq(
      "com.google.apis" % "google-api-services-pubsub" % "v1-rev11-1.22.0"
    )
  )

  lazy val pubsub = Project(
    id = "pubsub-core",
    base = file("core"),
    settings = baseSettings
  )

}
