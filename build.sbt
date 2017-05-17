import com.typesafe.config.ConfigFactory
import scala.util.{Try, Success, Failure}

val btVersion: String = {
  Try(ConfigFactory.load.getString("version")) match {
    case Success(ver) => ver
    case Failure(_) => "0.1.0"
  }
}

name := "http-verbs"
version := btVersion
scalaVersion := "2.11.11"
organization := "com.cjww-dev.libs"

val cjwwDep: Seq[ModuleID] = Seq(
  "com.cjww-dev.libs" % "logging_2.11" % "0.5.0",
  "com.cjww-dev.libs" % "bootstrapper_2.11" % "1.4.2",
  "com.cjww-dev.libs" % "data-security_2.11" % "0.10.0"
)

val testDep: Seq[ModuleID] = Seq(
  "org.scalatestplus.play" % "scalatestplus-play_2.11" % "2.0.0" % Test,
  "org.mockito" % "mockito-core" % "2.7.22" % Test
)

libraryDependencies ++= testDep
libraryDependencies ++= cjwwDep

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

bintrayOrganization := Some("cjww-development")
bintrayReleaseOnPublish in ThisBuild := true
bintrayRepository := "releases"
bintrayOmitLicense := true
