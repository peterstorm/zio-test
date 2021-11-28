package ziotest.capabilities.database

import doobie.hikari.HikariTransactor
import doobie.util.transactor.Transactor
import doobie.util.ExecutionContexts
import zio.{Task, Has, Managed, ZManaged, ZIO, URIO, ZLayer}
import zio.interop.catz._
import zio.interop.catz.implicits._
import ziotest.capabilities.config._
import scala.concurrent.ExecutionContext

object Database:

  type Database = Has[Transactor[Task]]

  private def makeTransactor(
    config: DatabaseConfig.Config,
    conExContext: ExecutionContext
  ): Managed[Throwable, Transactor[Task]] =
    HikariTransactor
      .newHikariTransactor[Task](
        config.driver,
        config.url,
        config.username,
        config.password,
        conExContext
      )
      .toManagedZIO

  val managedTransactor: ZManaged[DatabaseConfig, Throwable, Transactor[Task]] =
    for
      config <- getDatabaseConfig.toManaged_
      conExContext <- ExecutionContexts.fixedThreadPool[Task](32).toManagedZIO
      transactor <- makeTransactor(config, conExContext)
    yield transactor

  val live: ZLayer[DatabaseConfig, Throwable, Database] =
    ZLayer.fromManaged(managedTransactor)

  val transactor: URIO[Database, Transactor[Task]] = ZIO.service
