package ziotest.domain.errors

import pureconfig.error.ConfigReaderFailures

final case class ConfigLoadError(
  failures: ConfigReaderFailures
) extends BaseError(s"Configuration load failed with: ${failures.toList.map(_.description).mkString(", ")}")
