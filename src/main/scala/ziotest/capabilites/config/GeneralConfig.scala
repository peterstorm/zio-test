package ziotest.capabilities.config

import pureconfig._
import zio.{ZLayer}

object GeneralConfig:

  final case class Config(
    processRerun: String
  )

  object Config:

    given ConfigReader[Config] =
      ConfigReader.forProduct1("processRerun")(Config(_))

  val fromAppConfig: ZLayer[AppConfig, Nothing, GeneralConfig] =
    ZLayer.fromService(_.general)
