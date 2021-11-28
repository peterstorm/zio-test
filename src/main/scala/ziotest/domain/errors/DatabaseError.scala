package ziotest.domain.errors

final case class DatabaseError(
  message: String
) extends BaseError(message)
