package ziotest.capabilities.config

import pureconfig._
import zio.{ZLayer, Has, ZIO}

import ziotest.domain.errors.ConfigLoadError

object AppConfig:

  final case class Config(
    database: DatabaseConfig.Config,
    general: GeneralConfig.Config
  )

  object Config:

    given ConfigReader[Config] =
      ConfigReader.forProduct2("database", "general")(Config(_,_))

  val live: ZLayer[Any, ConfigLoadError, AppConfig] =
    ZLayer.fromEffect(
      ZIO
        .fromEither(ConfigSource.default.load[Config])
        .mapError(ConfigLoadError(_))
    )
