package ziotest.capabilities.http

import io.circe.syntax._
import io.circe.{Encoder, Json, Printer}
import org.http4s._
import org.http4s.headers.`Content-Type`
import org.typelevel.ci.CIString

import scala.collection.immutable.ListMap

object HttpClientSyntax:

  extension[R](req: RequestBuilder[R])
    def toHeaderList: List[Header.Raw] =
      (req.headers.map(kv => Header.Raw(CIString(kv._1), kv._2)) ++
        req.authHeader.map(kv => Header.Raw(CIString(kv._1), kv._2))).toList

    def toUri: Uri =
      val queryString = 
        req.params.toList.map{ case (k, v) =>
          s"$k=$v"
        }
        .mkString("&")
      val query = Query.unsafeFromString(Uri.encode(queryString))
      Uri
        .unsafeFromString(req.url)
        .copy(query = query)

  extension[F[_]](req: Request[F])
    def withJsonBody[T](maybeData: Option[T])(using enc: Encoder[T]): Request[F] =
      maybeData.fold(req)(data =>
          req
            .withContentType(`Content-Type`(MediaType.application.json))
            .withEntity(data.asJson.noSpacesNorNull)
      )

  extension(json: Json)
    def noSpacesNorNull: String = Printer.noSpaces.copy(dropNullValues = true).print(json)
