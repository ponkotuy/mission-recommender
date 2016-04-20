
name := "MissionRecommender"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  ws,
  "org.skinny-framework" %% "skinny-orm" % "2.0.+",
  "org.scalikejdbc" %% "scalikejdbc-play-initializer" % "2.5.0",
  "org.mariadb.jdbc" % "mariadb-java-client" % "1.4.0",
  "org.flywaydb" %% "flyway-play" % "3.0.0"
)

routesGenerator := InjectedRoutesGenerator

lazy val root = (project in file(".")).enablePlugins(PlayScala)
