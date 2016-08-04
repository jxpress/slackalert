name := "slackalert"

organization := "net.jxpress"

version := "0.0.1"

scalaVersion := "2.11.8"

resolvers += "Maven Repository on Github" at "https://jxpress.github.io/mvnrepos/"

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.2.1",
  "org.json4s" %% "json4s-jackson" % "3.3.0",
  "org.scalaj" % "scalaj-http_2.11" % "2.2.1",
  "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test"
)

mavenRepositoryName := "mvnrepos"
gitHubURI := "git@github.com:jxpress/mvnrepos.git"
MvnReposOnGitHubPlugin.projectSettings

publishMavenStyle := true

publishArtifact in (Compile, packageBin) := true
publishArtifact in (Compile, packageDoc) := false
publishArtifact in (Compile, packageSrc) := false
publishArtifact in Test := false