package io.lbert.app.controllers.v2

import java.util.UUID
import javax.inject.Inject
import io.lbert.app.services.ArtistService
import io.lbert.play.controllers._

class ArtistController @Inject()(tFSchemaIOInjector: SchemaInjector,
                                 service: ArtistService) {

  import io.lbert.app.models.ArtistJson._
  import tFSchemaIOInjector._

  val artistRestSchema = SchemaREST(artistFormat,searchArtistMapping,"artist")

  def create           = Schema(service.create _)(artistRestSchema.create)
  def get(id: UUID)    = Schema(service.get(id))(artistRestSchema.get)
  def search           = Schema(service.search _)(artistRestSchema.search)
  def update(id: UUID) = Schema(service.update(id) _)(artistRestSchema.update)
  def delete(id: UUID) = Schema(service.delete(id))(artistRestSchema.delete)
}
