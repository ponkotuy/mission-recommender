
name := "MissionRecommender"

scalaVersion := "2.11.11"

libraryDependencies ++= Seq(
  ws,
  cache,
  "org.skinny-framework" %% "skinny-orm" % "2.3.6",
  "org.scalikejdbc" %% "scalikejdbc-play-initializer" % "2.5.1",
  "org.mariadb.jdbc" % "mariadb-java-client" % "1.5.9",
  "org.flywaydb" %% "flyway-play" % "3.1.0",
  "jp.t2v" %% "play2-auth" % "0.14.2",
  "com.google.maps" % "google-maps-services" % "0.1.20",
  "org.scalatest" %% "scalatest" % "3.0.3" % "test",
  "org.scalacheck" %% "scalacheck" % "1.13.5" % "test"
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
