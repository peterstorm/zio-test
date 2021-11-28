package ziotest.repository

import zio.{Has, RIO, Task}
import ziotest.domain.mail._

object MailRepository:

  type MailRepository = Has[MailRepository.Service]

  trait Service:

    def getMailMessages: Task[List[MailMessage]]

    def getMailContent(mailMessage: MailMessage): Task[List[MailContent]]

    def commitMailMessagesSent(mailMessage: MailMessage, requestId: String): Task[Unit]

    def setMailMessageError(mailMessage: MailMessage, error: String): Task[Unit]

  def getMailMessages: RIO[MailRepository, List[MailMessage]] = RIO.accessM(_.get.getMailMessages)

  def getMailContent(mailMessage: MailMessage): RIO[MailRepository, List[MailContent]] = RIO.accessM(_.get.getMailContent(mailMessage))

  def commitMailMessagesSent(mailMessage: MailMessage, requestId: String): RIO[MailRepository, Unit] = 
    RIO.accessM(_.get.commitMailMessagesSent(mailMessage, requestId))

  def setMailMessageError(mailMessage: MailMessage, error: String): RIO[MailRepository, Unit] = 
    RIO.accessM(_.get.setMailMessageError(mailMessage, error))
