package ziotest.domain.auth

final case class ClientCredentials(
  clientId: ClientId,
  clientSecret: ClientSecret
)
