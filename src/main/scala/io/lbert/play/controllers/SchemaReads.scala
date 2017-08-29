package io.lbert.play.controllers

import io.lbert.play.controllers.ReaderMonad._
import play.api.data.{Mapping, Form => PlayForm}
import play.api.libs.json.{JsPath, Reads}
import scala.concurrent.Future
import scalaz.ReaderT

trait SchemaReads[A] {

  def reader: ReaderMonad[A]

  def map[B](f: A => B): SchemaReads[B] =
    SchemaReads(ReaderT { deps =>
      implicit val ec = deps.executionContext
      reader.run(deps).map(f)
    })

  def flatMap[B](f: A => SchemaReads[B]): SchemaReads[B] =
    SchemaReads(ReaderT { deps =>
      implicit val ec = deps.executionContext
      reader.run(deps).flatMap(a => f(a).reader.run(deps))
    })
}

object SchemaReads {

  private def apply[A](r: ReaderMonad[A]): SchemaReads[A] = new SchemaReads[A] {
    override def reader: ReaderMonad[A] = r
  }

  trait EmptySchemaReads extends SchemaReads[Unit] {

    private val schemaReads = this

    trait SchemaResource[A] extends SchemaReads[A] {

    }

    object SchemaResource {
      def apply[A](a: ReaderMonad[A]): SchemaResource[A] = new SchemaResource[A] {
        override def reader: ReaderMonad[A] = a
      }
    }

    case class JsonSchemaResource[A](reads: Reads[A]) extends SchemaResource[A] {
      def withKey(key: String): JsonSchemaResource[A] =
        copy((JsPath \ key).read[A](reads))

      override def reader: ReaderMonad[A] = json(reads)
    }

    object Form {
      def apply[A](mapping: Mapping[A]): SchemaResource[A] =
        SchemaResource(playForm(PlayForm(mapping)))
    }

    object JSON {
      def apply[A](reads: Reads[A]): JsonSchemaResource[A] =
        JsonSchemaResource(reads)
    }
  }

  def apply(): EmptySchemaReads = new EmptySchemaReads {
    override def reader: ReaderMonad[Unit] = ReaderT(_ => Future.successful(()))
  }
}
