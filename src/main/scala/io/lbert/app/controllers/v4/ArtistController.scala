package io.lbert.app.controllers.v4

import javax.inject.Inject
import io.lbert.app.services.ArtistService
import io.lbert.play.controllers.{Schema, SchemaInjector}
import scala.concurrent.ExecutionContext

class ArtistController @Inject() (tFSchemaIOInjector: SchemaInjector,
                                  service: ArtistService)
                                 (implicit ec: ExecutionContext) {
  import io.lbert.app.controllers.v1.ArtistSchemas._
  import tFSchemaIOInjector._

  def create = ???
}

case class SchemaWrapper[A,B](schema: Schema[A,B])
