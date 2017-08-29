package io.lbert.app.controllers.v4

import javax.inject.Inject
import io.lbert.app.services.ArtistService
import io.lbert.play.controllers.{Schema, ActionInjector}
import scala.concurrent.ExecutionContext

class ArtistController @Inject() (actionInjector: ActionInjector,
                                  service: ArtistService)
                                 (implicit ec: ExecutionContext) {
  import io.lbert.app.controllers.v1.ArtistSchemas._
  import actionInjector._

  def create = ???
}

case class SchemaWrapper[A,B](schema: Schema[A,B])
