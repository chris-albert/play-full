organization := "io.lbert"

name := """play-full"""

version := "1.0.7"

scalaVersion := "2.11.11"

val playVersion = "2.5.12"

resolvers ++= Seq(
  "TFly Github Repo"                       at "http://ticketfly.github.com/repo",
  "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
)


lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  "com.typesafe.play"      %% "play-ws"                           % playVersion % Provided, // Do not add particular version of Play as transitive dependency
  "com.typesafe.play"      %% "play"                              % playVersion % Provided,
  "org.scalatest"          %% "scalatest"                         % "3.0.1"     % Test,
  "org.scalatestplus.play" %% "scalatestplus-play"                % "2.0.0"     % Test,
  "org.mockito"             % "mockito-core"                      % "1.9.5"     % Test,
  "org.scalaz"             %% "scalaz-core"                       % "7.2.12"
)

//Scoverage configuration
coverageEnabled.in(ThisBuild ,Test, test) := true

coverageFailOnMinimum := true

coverageExcludedPackages := ".*javascript.*;router;.*BuildInfo;.*Reverse.*"

//So we can use sbt project style (./src not ./app)
disablePlugins(PlayLayoutPlugin)