package ziotest.domain.mail

final case class Mail(
  mailMessage: MailMessage,
  content: String
)
