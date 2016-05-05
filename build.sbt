
name := "MissionRecommender"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  ws,
  cache,
  "org.skinny-framework" %% "skinny-orm" % "2.0.+",
  "org.scalikejdbc" %% "scalikejdbc-play-initializer" % "2.5.0",
  "org.mariadb.jdbc" % "mariadb-java-client" % "1.4.0",
  "org.flywaydb" %% "flyway-play" % "3.0.0",
  "jp.t2v" %% "play2-auth" % "0.14.2",
  "com.google.maps" % "google-maps-services" % "0.1.12",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test"
)

routesGenerator := InjectedRoutesGenerator

lazy val root = (project in file(".")).enablePlugins(PlayScala)

initialCommands in console := """
import utils.Tools._
import scalikejdbc.config.DBs
DBs.setupAll()
"""
