package ziotest.domain.config

import pureconfig._

final case class DatabaseConfig(
  driver: String,
  url: String,
  username: String,
  password: String
)

object DatabaseConfig:

  given ConfigReader[DatabaseConfig] =
    ConfigReader.forProduct4("driver", "url", "username", "password")(DatabaseConfig(_,_,_,_))
