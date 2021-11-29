package ziotest.capabilites.http

import org.http4s._


case class RequestBuilder[A](
  url: String,
  httpVerb: Method = Method.GET,
  authHeader: Map[String, String] = Map.empty[String, String],
  data: Option[A] = None,
  params: Map[String, String] = Map.empty[String, String],
  headers: Map[String, String] = Map.empty[String, String]
):

  def postMethod: RequestBuilder[A] = 
    this.copy(httpVerb = Method.POST)

  def putMethod: RequestBuilder[A] = 
    this.copy(httpVerb = Method.PUT)

  def deleteMethod: RequestBuilder[A] = 
    this.copy(httpVerb = Method.DELETE)

  def withHeaders(headers: Map[String, String]): RequestBuilder[A] =
    this.copy(headers = headers)

  def withParams(params: Map[String, String]): RequestBuilder[A] =
    this.copy(params = params)

  def withData(data: A): RequestBuilder[A] = 
    this.copy(data = Some(data))

  def withAuth(authToken: Option[String] = None): RequestBuilder[A] =
    this.copy(authHeader = authToken match
      case Some(token) => Map("Authorization" -> s"Bearer $token")
      case _           => Map.empty[String, String]
    )
