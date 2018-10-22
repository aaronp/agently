import mill._
import mill.scalalib._
import mill.scalalib.publish._
import mill.scalalib.scalafmt._


// http://www.lihaoyi.com/mill/index.html

object const {
  def ScalaTwelve = "2.12.7"
  def ScalaVersion = ScalaTwelve
}

object agently extends ScalaModule with ScalafmtModule with PublishModule {
  def scalaVersion = const.ScalaVersion

  //def forkArgs = Seq("-Xmx1g")
  override def scalacOptions = Seq("-deprecation", "-feature")
  
  def mainClass = Some("agently.AgentlyMain")

  val circe = List("core", "generic", "parser", "optics", "java8").map { artifact =>
    ivy"io.circe::circe-$artifact:0.10.0"
  }

  def ivyDeps = Agg(
    ivy"org.reactivestreams:reactive-streams:1.0.2",
    ivy"com.typesafe.scala-logging::scala-logging:3.7.2",
    ivy"ch.qos.logback:logback-classic:1.1.11",
    ivy"io.get-coursier::coursier:1.0.3",
    ivy"io.get-coursier::coursier-cache:1.0.3"
  ) ++ circe

  def publishVersion = "0.0.1"
  def pomSettings = PomSettings(
    description = "Agently launcher",
    organization = "com.github.aaronp",
    url = "https://github.com/aaronp/agently",
    licenses = Seq(License.MIT),
    versionControl = VersionControl.github("aaronp", "agentlyagently"),
    developers = Seq(
      Developer("aaronp", "Aaron Pritzlaff","https://github.com/aaronp")
    )
  )


  object test extends Tests {
    def ivyDeps = Agg(ivy"org.scalatest::scalatest:3.0.4")
    def testFrameworks = Seq("org.scalatest.tools.Framework")
  }

}