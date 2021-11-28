package ziotest.domain.config

import pureconfig._

final case class AppConfig(
  database: DatabaseConfig,
  general: GeneralConfig
)

object AppConfig:

  given ConfigReader[AppConfig] =
    ConfigReader.forProduct2("database", "general")(AppConfig(_,_))
