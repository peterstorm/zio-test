package ziotest

import org.http4s.blaze.client.BlazeClientBuilder
import org.http4s.client.Client
import ziotest.capabilities.config._
import ziotest.capabilities.database._
import zio._
import zio.system._
import zio.interop.catz._
import zio.interop.catz.implicits._
import ziotest.repository.AuthRepositoryDoobie
import ziotest.repository.MailRepositoryDoobie
import ziotest.services.MailServiceLive

object Main extends zio.App:

  val appConfig = AppConfig.live
  val database = appConfig >>> Database.live
  val authRepo = database >>> AuthRepositoryDoobie.live
  val mailRepo = database >>> MailRepositoryDoobie.live
  val mailService = mailRepo >>> MailServiceLive.layer

  private def makeHttpClient: UIO[TaskManaged[Client[Task]]] =
    ZIO
      .runtime[Any]
      .map { implicit rts =>
        BlazeClientBuilder
          .apply[Task](platform.executor.asEC)
          .resource
          .toManagedZIO
      }

  def run(args: List[String]): URIO[ZEnv, ExitCode] =
    ZIO.succeed(println("hello world")).exitCode

