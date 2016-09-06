import sbt._
import Keys._


object Versions {
  val akka = "2.4.9"
}


object build extends Build {

  val baseSettings = Seq(
    organization := "mn.clarkson",
    scalaVersion := "2.11.8",
    libraryDependencies ++= Seq(
      "com.google.apis" % "google-api-services-pubsub" % "v1-rev11-1.22.0"
    )
  )

  val akka = Seq(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % Versions.akka
    )
  )

  lazy val pubsub = Project(
    id = "pubsub-core",
    base = file("core"),
    settings = baseSettings
  )

  lazy val pubsubAkka = Project(
    id = "pubsub-akka",
    base = file("akka"),
    settings = baseSettings ++ akka
  ) dependsOn(pubsub)

}
