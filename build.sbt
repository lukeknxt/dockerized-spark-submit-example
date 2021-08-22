import Dependencies._

Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val root = (project in file("."))
  .settings(
    name := "dockerize-spark-submit-example",
    scalaVersion     := "2.12.13",
    version          := "0.1.0-SNAPSHOT",
    organization     := "net.lukeknight",
    libraryDependencies ++= Dependencies.main,
    libraryDependencies ++= Dependencies.test,
    artifactName := { (sv: ScalaVersion, module: ModuleID, artifact: Artifact) =>
      artifact.name + "." + artifact.extension
    }
  )
