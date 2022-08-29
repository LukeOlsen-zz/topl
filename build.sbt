ThisBuild / version := "0.1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "com.lihaoyi"       %% "upickle"   % "2.0.0",
  "com.lihaoyi"       %% "os-lib"    % "0.8.1",
  "com.typesafe.play" %% "play-json" % "2.9.2",
  "org.scalameta"     %% "munit"     % "0.7.29" % Test
)

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "ToplTraffic"
  )
