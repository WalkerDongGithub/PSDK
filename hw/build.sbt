ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.13"

lazy val hw = (project in file("."))
  .settings(
    name := "hw",
    idePackagePrefix := Some("psdk.hw"),
    scalacOptions ++= Seq(
      "-deprecation",
      "-feature",
      "-unchecked",
      "-Xfatal-warnings",
      "-language:reflectiveCalls"
    ),
    addCompilerPlugin("edu.berkeley.cs" % "chisel3-plugin" % "3.5.3" cross CrossVersion.full),
    libraryDependencies += "edu.berkeley.cs" %% "chisel3" % "3.5.3",
    libraryDependencies += "edu.berkeley.cs" %% "chiseltest" % "0.5.3"
  )
