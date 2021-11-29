package ziotest.capabilities.config

import pureconfig._
import zio.{ZLayer}

object DatabaseConfig:

  final case class Config(
    driver: String,
    url: String,
    username: String,
    password: String
  )

  object Config:

    given ConfigReader[Config] =
      ConfigReader.forProduct4("driver", "url", "username", "password")(Config(_,_,_,_))

  val fromAppConfig: ZLayer[AppConfig, Nothing, DatabaseConfig] =
    ZLayer.fromService(_.database)
