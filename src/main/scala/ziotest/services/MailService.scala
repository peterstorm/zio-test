package ziotest.service

import zio.{Has, RIO}
import ziotest.repository.MailRepository.MailRepository
import ziotest.domain.mail._
import zio.logging.Logging

object MailService:

  type MailService = Has[MailService.Service with Logging]

  trait Service:

    def getPendingMails: RIO[MailRepository, List[Mail]]

    def commitMailMessagesSent(mailMessage: MailMessage, requestId: String): RIO[MailRepository, Unit]

    def setMailMessageError(mailMessage: MailMessage, error: String): RIO[MailRepository, Unit]

  def getPendingMails: RIO[MailService with MailRepository, List[Mail]] =
    RIO.accessM(_.get.getPendingMails)

  def commitMailMessagesSent(mailMessage: MailMessage, requestId: String): RIO[MailService with MailRepository, Unit] =
    RIO.accessM(_.get.commitMailMessagesSent(mailMessage, requestId))

  def setMailMessageError(mailMessage: MailMessage, error: String): RIO[MailService with MailRepository, Unit] =
    RIO.accessM(_.get.setMailMessageError(mailMessage, error))
