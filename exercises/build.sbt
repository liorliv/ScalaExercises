ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.11"

lazy val root = (project in file("."))
  .settings(
    name := "exercises"
  )

libraryDependencies += "org.scalameta" %% "munit" % "0.7.22" % Test
libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.14.0"

testFrameworks += new TestFramework("munit.Framework")
