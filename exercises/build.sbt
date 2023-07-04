ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.11"

lazy val root = (project in file("."))
  .settings(
    name := "exercises"
  )

libraryDependencies += "org.scalameta" %% "munit" % "0.7.22" % Test
libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.14.0"
libraryDependencies += "com.typesafe.akka" %% "akka-actor-typed" % "2.6.15"
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.6.15"
libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.2.6"
libraryDependencies += "org.jsoup" % "jsoup" % "1.14.3"


testFrameworks += new TestFramework("munit.Framework")
