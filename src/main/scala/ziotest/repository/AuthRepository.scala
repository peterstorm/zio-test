package ziotest.repository

import doobie.util.transactor.Transactor
import zio.{Has, RIO, ZIO, Task}
import ziotest.domain.auth.ClientCredentials

trait AuthRepository:

  def getClientCredentials: Task[ClientCredentials]

object AuthRepository:

  def getClientCredentials =
    ZIO.serviceWith[AuthRepository](_.getClientCredentials)

