package ziotest.services

import ziotest.service.MailService
import ziotest.repository.MailRepository.MailRepository
import ziotest.repository.MailRepository
import zio.{Has, RIO, Task, ZLayer, URLayer}
import ziotest.domain.mail._
import zio.interop.catz._
import cats.syntax.all._
import zio.Function1ToLayerSyntax

final case class MailServiceLive(
  repo: MailRepository.Service,
) extends MailService:

  def getPendingMails: Task[List[Mail]] =
    repo.getMailMessages.flatMap(l =>
      l.traverse(mm =>
        repo.getMailContent(mm).map(mc =>
          Mail(mm, mc.foldLeft("")((b, a) => b |+| a.message))
        )
      )
    )

  def commitMailMessageSent(
    mailMessage: MailMessage,
    requestId: String
  ): Task[Unit] =
    repo.commitMailMessageSent(mailMessage, requestId)

  def setMailMessageError(
    mailMessage: MailMessage,
    error: String
  ): Task[Unit] =
    repo.setMailMessageError(mailMessage, error)

object MailServiceLive:

  val layer: URLayer[Has[MailRepository.Service], Has[MailService]] =
    (MailServiceLive(_)).toLayer
  

  
