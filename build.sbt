import com.typesafe.config.ConfigFactory
import scala.util.{Try, Success, Failure}

val btVersion: String = {
  Try(ConfigFactory.load.getString("version")) match {
    case Success(ver) => ver
    case Failure(_) => "INVALID_RELEASE_VERSION"
  }
}

name := "http-verbs"
version := btVersion
scalaVersion := "2.11.8"
organization := "com.cjww-dev.libs"

val cjwwDep: Seq[ModuleID] = Seq("com.cjww-dev.libs" % "data-security_2.11" % "0.3.0")

val codeDep: Seq[ModuleID] = Seq(
  "ch.qos.logback" % "logback-classic" % "1.1.7",
  "com.typesafe.play" % "play_2.11" % "2.5.12"
)

val testDep: Seq[ModuleID] = Seq(
  "org.scalatestplus.play" % "scalatestplus-play_2.11" % "1.5.1" % Test,
  "org.mockito" % "mockito-core" % "2.2.29" % Test
)

libraryDependencies ++= codeDep
libraryDependencies ++= testDep
libraryDependencies ++= cjwwDep

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

bintrayOrganization := Some("cjww-development")
bintrayReleaseOnPublish in ThisBuild := false
bintrayRepository := "releases"
bintrayOmitLicense := true
