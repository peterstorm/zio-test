package ziotest.capabilities

import zio._

package object config:

  type AppConfig = Has[AppConfig.Config]
  type DatabaseConfig = Has[DatabaseConfig.Config]
  type GeneralConfig = Has[GeneralConfig.Config]

  val getAppConfig: URIO[AppConfig, AppConfig.Config] =
    ZIO.access(_.get)

  val getDatabaseConfig: URIO[DatabaseConfig, DatabaseConfig.Config] =
    ZIO.access(_.get)

  val getGeneralConfig: URIO[GeneralConfig, GeneralConfig.Config] =
    ZIO.access(_.get)
