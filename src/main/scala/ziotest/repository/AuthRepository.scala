package ziotest.repository

import doobie.util.transactor.Transactor
import zio.{Has, RIO, ZIO, Task}
import ziotest.domain.auth.ClientCredentials

object AuthRepository:

  type AuthRepository = Has[AuthRepository.Service]

  trait Service:

    def getClientCredentials: Task[ClientCredentials]

  def getClientCredentials: RIO[AuthRepository, ClientCredentials] = 
    RIO.accessM(_.get.getClientCredentials)
