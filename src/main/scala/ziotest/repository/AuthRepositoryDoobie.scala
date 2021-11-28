package ziotest.repository

import doobie.util.transactor.Transactor
import zio.{Task, ZLayer}
import ziotest.domain.auth._
import ziotest.domain.errors.DatabaseError
import ziotest.capabilities.database.Database._
import ziotest.capabilities.database._
import ziotest.repository.AuthRepository.AuthRepository
import doobie.util.query.Query0
import doobie.syntax.all._
import doobie.util.Read
import AuthRepositoryDoobie.SqlQueries
import zio.interop.catz._

final case class AuthRepositoryDoobie(
  tx: Transactor[Task]
) extends AuthRepository.Service:

  def getClientCredentials: Task[ClientCredentials] =
    val action =
      for
        id <- SqlQueries.getParamString[ClientId]("PROVIDER.CLIENT_ID").unique
        secret <- SqlQueries.getParamString[ClientSecret]("PROVIDER.CLIENT_SECRET").unique
      yield ClientCredentials(id, secret)
    action
      .transact(tx)
      .mapError(err => DatabaseError(err.getMessage))


object AuthRepositoryDoobie:

  val live: ZLayer[Database, Throwable, AuthRepository] =
    ZLayer.fromEffect(Database.transactor.map(AuthRepositoryDoobie(_)))

  object SqlQueries:

    def getParamString[A: Read](key: String): Query0[A] = 
      sql"""
        SELECT CORE_PARAMETER.GET_STRING($key) PARAMETER_VALUE_STR FROM DUAL
      """.query[A]

