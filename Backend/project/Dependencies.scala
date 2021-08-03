import sbt._

object Dependencies {

  val typelevelOrg  = "org.typelevel"
  val http4sOrg     = "org.http4s"
  val fs2Org        = "co.fs2"
  val circeOrg      = "io.circe"
  val pureconfigOrg = "com.github.pureconfig"

  val catsCoreV   = "2.6.1"
  val catsEffV    = "2.5.1"
  val http4sV     = "0.21.25"
  val fs2V        = "2.5.9"
  val circeV      = "0.14.1"
  val pureconfigV = "0.16.0"

  def http4sDep(moduleName: String): ModuleID =
    http4sOrg %% ("http4s-" + moduleName) % http4sV
  def http4sBlaze(blazeModule: String): ModuleID =
    http4sDep("blaze-" + blazeModule)
  def circeDep(moduleName: String): ModuleID =
    circeOrg %% ("circe-" + moduleName) % circeV

  val catsCore          = typelevelOrg %% "cats-core" % catsCoreV
  val catsEffect        = typelevelOrg %% "cats-effect" % catsEffV

  val fs2               = fs2Org %% "fs2-core" % fs2V

  val pureconfig        = pureconfigOrg %% "pureconfig" % pureconfigV

  val http4sCore        = http4sDep("core")
  val http4sCirce       = http4sDep("circe")
  val http4sDsl         = http4sDep("dsl")
  val http4sBlazeServer = http4sBlaze("server")
  val http4sBlazeClient = http4sBlaze("client")
  val http4sServer      = http4sDep("server")

  val circeCore         = circeDep("core")
  val circeGeneric      = circeDep("generic")
  val circeParser       = circeDep("parser")

  val core = Seq(catsCore, catsEffect, fs2)
  val http4s = Seq(http4sCore, http4sCirce, http4sDsl, http4sBlazeServer, http4sBlazeClient, http4sServer)
  val circe = Seq(circeCore, circeGeneric, circeParser)

  
}
