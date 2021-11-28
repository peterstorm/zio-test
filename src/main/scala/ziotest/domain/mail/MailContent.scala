package ziotest.domain.mail

final case class MailContent(
  id: String,
  messageId: String,
  sequence: String,
  message: String
)
