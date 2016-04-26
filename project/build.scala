import sbt._
import Keys._
import scala.util.Properties

object ApplicationBuild extends Build {

  /* Projects */
  lazy val root = Project("http4s-websocket", file("."))
    .settings(buildSettings: _*)

  val jvmTarget = TaskKey[String]("jvm-target-version", "Defines the target JVM version for object files.")

  /* global build settings */
  lazy val buildSettings = publishing ++ Seq(
    organization := "org.http4s",

    version := "0.2.0-SNAPSHOT",

    scalaVersion := "2.11.8",

    crossScalaVersions := Seq("2.10.6", "2.11.8", "2.12.0-M4"),

    description := "common websocket support for variou sservers",

    homepage := Some(url("https://github.com/http4s/http4s-websocket")),

    startYear := Some(2014),

    licenses := Seq(("Apache 2.0", url("http://www.apache.org/licenses/LICENSE-2.0"))),

    scmInfo := Some(
      ScmInfo(
        url("https://github.com/http4s/http4s-websocket"),
        "scm:git:https://github.com/http4s/http4s-websocket.git",
        Some("scm:git:git@github.com:http4s/http4s-websocket.git")
      )
    ),

    jvmTarget <<= scalaVersion.map {
      VersionNumber(_).numbers match {
        case Seq(2, 10, _*) => "1.7"
        case _ => "1.8"
      }
    },

    scalacOptions in ThisBuild <<= jvmTarget.map { jvm => Seq(
      "-feature",
      "-deprecation",
      "-unchecked",
      "-language:implicitConversions",
      s"-target:jvm-$jvm"
    )},

    fork in run := true,

    libraryDependencies += specs2Core(scalaVersion.value) % "test"
  )

  def specs2Core(sv: String) = "org.specs2" %% "specs2-core" % (sv match {
    case "2.12.0-M4" => "3.7.3.1"
    case _ => "3.7.3"
  })

  /* publishing */
  lazy val publishing = Seq(
    publishMavenStyle := true,
    publishTo <<= version { (v: String) =>
      val nexus = "https://oss.sonatype.org/"
      if (v.trim.endsWith("SNAPSHOT")) Some(
        "snapshots" at nexus + "content/repositories/snapshots"
      )
      else Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => false },

    Seq("SONATYPE_USERNAME", "SONATYPE_PASSWORD") map Properties.envOrNone match {
      case Seq(Some(user), Some(pass)) =>
        credentials += Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", user, pass)
      case _ => credentials in ThisBuild ~= identity
    },

    pomExtra := (
      <developers>
        <developer>
          <id>bryce-anderson</id>
          <name>Bryce L. Anderson</name>
          <email>bryce.anderson22@gmail.com</email>
        </developer>
        <developer>
          <id>rossabaker</id>
          <name>Ross A. Baker</name>
          <email>ross@rossabaker.com</email>
        </developer>
      </developers>
    )
  )
}
