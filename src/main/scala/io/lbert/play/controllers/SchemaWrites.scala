package io.lbert.play.controllers

import io.lbert.play.controllers.SchemaWrites.PlayFullWrites
import play.api.http.Status._
import play.api.libs.json.{JsValue, Json, Writes}
import play.api.mvc.{Result, Results}
import scalaz.Reader

trait SchemaWrites[A] {

  def writes: PlayFullWrites[A]
  def result: Result = Results.Status(OK)

  object Status {
    def apply(code: Int): SchemaWrites[A] =
      withStatus(code)
    def Ok: SchemaWrites[A] = withStatus(OK)
    def Created: SchemaWrites[A] = withStatus(CREATED)
    def NoContent: SchemaWrites[A] = withStatus(NO_CONTENT)
  }

  private val self = this
  private def withStatus(s: Int): SchemaWrites[A] = new SchemaWrites[A] {
    override def writes: PlayFullWrites[A] = self.writes
    override def result: Result = self.result.copy(header = self.result.header.copy(status = s))
  }
}

object SchemaWrites {

  type JsResult = (Result,Option[JsValue])
  type PlayFullWrites[A] = Reader[A,Option[JsValue]]

  case class SchemaNoResource() extends SchemaWrites[Unit] {
    def JSON[A](writes: Writes[A]): SchemaJSONResource[A] =
      SchemaJSONResource(writes)

    override def writes: PlayFullWrites[Unit] = Reader(_ => None)
  }

  case class SchemaJSONResource[A](w: Writes[A],
                                   key: Option[String] = None) extends SchemaWrites[A] {
    override def writes: PlayFullWrites[A] =
      Reader { a =>
        val json = w.writes(a)
        val out = key.fold(json)(k => Json.obj(k -> json))
        Some(out)
      }

    def withKey(key: String): SchemaJSONResource[A] =
      copy(key = Some(key))

    def sequence(): SchemaJSONResource[Seq[A]] =
      copy(w = Writes.traversableWrites[A](w))
  }

  def apply(): SchemaNoResource = SchemaNoResource()
}
