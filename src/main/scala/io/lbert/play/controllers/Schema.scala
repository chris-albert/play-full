package io.lbert.play.controllers

import io.lbert.play.controllers.ReaderMonad.Dependencies
import io.lbert.play.controllers.SchemaWrites.JsResult
import play.api.libs.json.{Format, Reads, Writes}
import scala.concurrent.Future

case class Schema[A,B](reads: SchemaReads[A],
                       writes: SchemaWrites[B]) {

  def run(dependencies: Dependencies,
          block: A => Future[B]): Future[JsResult] = {
    implicit val ec = dependencies.executionContext
    for {
      a <- reads.reader.run(dependencies)
      b <- block(a)
    } yield writes.result -> writes.writes.run(b)
  }

  def JSON[C](key: String, format: Format[C]): Schema[C,C] =
    Schema(
      reads = SchemaReads().JSON(Reads(format.reads)).withKey(key),
      writes = SchemaWrites().JSON(Writes(format.writes)).withKey(key)
    )

  def mapWrites[C](f: SchemaWrites[B] => SchemaWrites[C]): Schema[A,C] =
    copy(writes = f(writes))

  def mapReads[C](f: SchemaReads[A] => SchemaReads[C]): Schema[C,B] =
    copy(reads = f(reads))
}

object Schema {

  def apply(): Schema[Unit,Unit] = Schema(SchemaReads(), SchemaWrites())

  def apply[A](reads: SchemaReads[A]): Schema[A,Unit] = apply().copy(reads = reads)

  def apply[A](writes: SchemaWrites[A]): Schema[Unit,A] = apply().copy(writes = writes)
}

