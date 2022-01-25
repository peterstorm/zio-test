package ziotest.repository

import doobie.util.transactor.Transactor
import zio.{Task, ZLayer}
import ziotest.domain.mail._
import ziotest.domain.errors.DatabaseError
import ziotest.capabilities.database.Database._
import ziotest.capabilities.database._
import ziotest.repository.MailRepository.MailRepository
import doobie.util.query.Query0
import doobie.util.update.Update0
import doobie.syntax.all._
import zio.interop.catz._
import java.time.format.DateTimeFormatter
import MailRepositoryDoobie.SQLQueries

final case class MailRepositoryDoobie(
  tx: Transactor[Task]
) extends MailRepository.Service:

  def getMailMessages: Task[List[MailMessage]] =
    SQLQueries
      .getMailMessagesSQL
      .to[List]
      .transact(tx)
      .mapError(err => DatabaseError(err.getMessage))

  def getMailContent(mailMessage: MailMessage): Task[List[MailContent]] =
    SQLQueries
      .getMailContentSQL(mailMessage)
      .to[List]
      .transact(tx)
      .mapError(err => DatabaseError(err.getMessage))

  def commitMailMessageSent(mailMessage: MailMessage, requestId: String): Task[Unit] =
    val action =
      for
        _ <- SQLQueries.deleteMailMessageSQL(mailMessage).run
        _ <- SQLQueries.insertMailMessageSentSQL(mailMessage, requestId).run
      yield ()
    action
      .transact(tx)
      .mapError(err => DatabaseError(err.getMessage))

  def setMailMessageError(mailMessage: MailMessage, error: String): Task[Unit] =
    SQLQueries
      .setMailMessageErrorSQL(mailMessage, error)
      .run
      .transact(tx)
      .unit
      .mapError(err => DatabaseError(err.getMessage))

object MailRepositoryDoobie:

  val live: ZLayer[Database, Throwable, MailRepository] =
    ZLayer.fromEffect(Database.transactor.map(MailRepositoryDoobie(_)))

  object SQLQueries:

    val receivedDate = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S")
    val date = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")

    def getMailMessagesSQL: Query0[MailMessage] = 
      sql"""
        SELECT 
          MM.ID, MM.STATUS_ID, MM.CREATE_DATE, MM.TO_ADDRESSES, MM.SUBJECT, MM.FROM_NAME, MM.FROM_EMAIL, MM.TYPE_ID,
          MM.ERROR_MESSAGE, MM.SEND_FROM_DATE, MM.FROM_USER, MT.MAIL_KEY, MM.MAILTEMPLATE_ID, MM.OBJECT_ID, MM.OBJECT_CLASS
        FROM 
          MAIL_MESSAGE MM, MAILTEMPLATE MT
        WHERE 
          MM.MAILTEMPLATE_ID IN (SELECT ID FROM MAILTEMPLATE WHERE MAIL_KEY LIKE 'SFMC%')
        AND MM.MAILTEMPLATE_ID = MT.ID
        AND MM.STATUS_ID IN (0, 70) 
        AND NVL(MM.SEND_FROM_DATE, SYSDATE) <= SYSDATE
      """.query[MailMessage]

    def getMailContentSQL(mailMessage: MailMessage): Query0[MailContent] = 
      sql"""
        SELECT
          ID, MESSAGE_ID, SEQ, MESSAGE
        FROM 
          MAIL_CONTENT
        WHERE MESSAGE_ID = ${mailMessage.id}
      """.query[MailContent]

    def deleteMailMessageSQL(mailMessage: MailMessage): Update0 = 
      sql"""
        DELETE
        FROM MAIL_MESSAGE
        WHERE
          ID = ${mailMessage.id}
      """.update

    def insertMailMessageSentSQL(mailMessage: MailMessage, sfRequestId: String): Update0 = 
      sql"""
        INSERT INTO MAIL_MESSAGE_SENT
          (ID, STATUS_ID, CREATE_DATE, SENT_DATE, TO_ADDRESSES, SUBJECT, MESSAGE, FROM_NAME, FROM_EMAIL, TYPE_ID
          , FROM_USER, MAILTEMPLATE_ID, OBJECT_ID, OBJECT_CLASS
          )
        VALUES(${mailMessage.id}, 1
          , TO_DATE(${date.format(receivedDate.parse(mailMessage.createDate))}, 'DD/MM/YYYY HH24:MI:SS')
          , SYSDATE, ${mailMessage.toAddresses}, ${mailMessage.subject}, $sfRequestId, ${mailMessage.fromName}
          , ${mailMessage.fromEmail}, ${mailMessage.typeId.toInt}, ${mailMessage.fromUser}, ${mailMessage.templateId}
          , ${mailMessage.objectId}, ${mailMessage.objectClass}
          )
      """.update

    def setMailMessageErrorSQL(mailMessage: MailMessage, error: String): Update0 = 
      sql"""
        UPDATE MAIL_MESSAGE
        SET
          STATUS_ID = 9, ERROR_MESSAGE = $error
        WHERE ID = ${mailMessage.id}
      """.update
