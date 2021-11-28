package ziotest.domain.config

import pureconfig._

final case class GeneralConfig(
  processRerun: String
)

object GeneralConfig:

  given ConfigReader[GeneralConfig] =
    ConfigReader.forProduct1("processRerun")(GeneralConfig(_))
