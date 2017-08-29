package io.lbert.play.controllers

import play.api.data.Mapping
import play.api.libs.json.{Format, Writes}

object SchemaREST {

  def apply[A,B](format: Format[A],
                 searchForm: Mapping[B],
                 key: String,
                 pluralKey: Option[String] = None): RESTSchemas[A,B] = {
    val createSchema = Schema()
      .JSON(key,format)
      .mapWrites(_.Status.Created)
    val getSchema = Schema(
      SchemaWrites()
        .JSON(Writes(format.writes)).withKey(key)
    )
    val searchSchema = Schema(
      reads = SchemaReads()
        .Form(searchForm),
      writes = SchemaWrites()
        .JSON(Writes(format.writes)).withKey(pluralKey.getOrElse(s"${key}s")).sequence()
    )
    val updateSchema = Schema().JSON(key,format)
    val deleteSchema = Schema().mapWrites(_.Status.NoContent)
    RESTSchemas[A,B](createSchema, getSchema, searchSchema, updateSchema, deleteSchema)
  }
}

case class RESTSchemas[A,B](create: Schema[A,A],
                            get: Schema[Unit,A],
                            search: Schema[B,Seq[A]],
                            update: Schema[A,A],
                            delete: Schema[Unit,Unit])
