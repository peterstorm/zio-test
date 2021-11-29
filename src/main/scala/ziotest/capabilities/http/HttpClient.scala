package ziotest.capabilities.http

import io.circe.{Decoder, Encoder}
import zio.{RIO, Has, Task}
import org.http4s.client.Client

trait HttpClient:

  def get[A: Decoder](
    method: String,
    headers: Map[String, String],
    params: Map[String, String]
  ): RIO[Has[Client[Task]], A]


  def post[A: Decoder, B: Encoder](
    url: String,
    headers: Map[String, String],
    data: B
  ): RIO[Has[Client[Task]], A]
