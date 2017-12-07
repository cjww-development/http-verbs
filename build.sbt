import com.typesafe.config.ConfigFactory
import scala.util.{Try, Success, Failure}

val libraryName = "http-verbs"

val btVersion: String = Try(ConfigFactory.load.getString("version")) match {
  case Success(ver) => ver
  case Failure(_)   => "0.1.0"
}

val dependencies: Seq[ModuleID] = Seq(
  "com.typesafe.play" % "play_2.11"                  % "2.5.16",
  "com.cjww-dev.libs" % "data-security_2.11"         % "2.10.0",
  "com.cjww-dev.libs" % "application-utilities_2.11" % "2.8.0"
)

val testDependencies: Seq[ModuleID] = Seq(
  "org.scalatestplus.play" % "scalatestplus-play_2.11" % "2.0.1"  % Test,
  "org.mockito"            % "mockito-core"            % "2.12.0" % Test
)

lazy val library = Project(libraryName, file("."))
  .settings(
    version                              :=  btVersion,
    scalaVersion                         :=  "2.11.12",
    organization                         :=  "com.cjww-dev.libs",
    resolvers                            +=  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
    libraryDependencies                  ++= dependencies ++ testDependencies,
    bintrayOrganization                  :=  Some("cjww-development"),
    bintrayReleaseOnPublish in ThisBuild :=  true,
    bintrayRepository                    :=  "releases",
    bintrayOmitLicense                   :=  true
  )
