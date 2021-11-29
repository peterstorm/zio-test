package ziotest.parser

import cats.parse.Parser._
import cats.parse.Rfc5234.{char => _, _}
import cats.parse.Rfc5234.{char as charR}
import cats.parse.{Parser as P}
import cats.parse.{Parser0 as P0}
import ziotest.domain.Attributes
import cats.syntax.all._

object MailParser:

  private val whitespace: P[Unit] = P.charIn(" \t\r\n").void
  private val whitespace0: P0[Unit] = whitespace.rep0.void

  val parser: P[Attributes] = P.recursive[Attributes] { recurse =>

    val allowedChars = List(
      char('-'),
      char(','),
      char('_'),
      char('/'),
      char('.'),
      char('å'),
      char('æ'),
      char('ø'),
      char('@'),
      char(':'),
      char('?'),
      char('!'),
      char('|'),
      char('='),
      sp,
      alpha,
      digit
    )

    val key = oneOf(allowedChars).rep.string
    val string = oneOf(allowedChars).rep.string
    val str = string.map(Attributes.AString(_))

    val listSep: P[Unit] =
      (whitespace0.with1.soft ~ char(';') ~ whitespace0).void

    def repeat[A](pa: P[A]): P0[List[A]] =
      whitespace0.soft *> pa.repSep0(listSep) <* whitespace0

    val list = repeat(recurse).with1
      .between(char('['), char(']'))
      .map(l => Attributes.AList(l))

    val kv: P[(String, Attributes)] =
      val seperator = char('¤')
      key ~ (seperator *> recurse)

    val map = repeat(kv).with1
      .between(char('{') *> whitespace0, whitespace0.with1.soft *> char('}'))
      .map(l => Attributes.AMap(l.toMap))

    oneOf(str :: list :: map :: Nil)

  }

  val parseFile: P[Attributes] =
    whitespace0.with1 *> parser <* (whitespace0 ~ P.end)


