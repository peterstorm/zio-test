package ziotest.services

import zio.{Has, RIO, ZIO, Task}
import ziotest.repository.MailRepository.MailRepository
import ziotest.domain.mail._

trait MailService:

  def getPendingMails: Task[List[Mail]]

  def commitMailMessageSent(mailMessage: MailMessage, requestId: String): Task[Unit]

  def setMailMessageError(mailMessage: MailMessage, error: String): Task[Unit]

object MailService:

  def getPendingMails = 
    ZIO.serviceWith[MailService](_.getPendingMails)

  def commitMailMessageSent(mailMessage: MailMessage, requestId: String) =
    ZIO.serviceWith[MailService](_.commitMailMessageSent(mailMessage, requestId))

  def setMailMessageError(mailMessage: MailMessage, error: String) =
    ZIO.serviceWith[MailService](_.setMailMessageError(mailMessage, error))
