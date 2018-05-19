
name := "MissionRecommender"

scalaVersion := "2.12.6"

libraryDependencies ++= Seq(
  ws,
  guice,
  "org.skinny-framework" %% "skinny-orm" % "2.6.0",
  "org.scalikejdbc" %% "scalikejdbc-play-initializer" % "2.6.0-scalikejdbc-3.2",
  "org.mariadb.jdbc" % "mariadb-java-client" % "1.5.9",
  "org.flywaydb" %% "flyway-play" % "5.0.0",
  "com.google.maps" % "google-maps-services" % "0.1.20",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test",
  "org.scalacheck" %% "scalacheck" % "1.14.0" % "test"
)

routesGenerator := InjectedRoutesGenerator

lazy val root = (project in file(".")).enablePlugins(PlayScala)

initialCommands in console := """
import utils.Tools._
import scalikejdbc.config.DBs
DBs.setupAll()
"""

// Docker settings
dockerRepository := Some("ponkotuy")
dockerUpdateLatest := true
