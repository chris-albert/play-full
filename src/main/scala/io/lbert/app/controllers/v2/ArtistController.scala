package io.lbert.app.controllers.v2

import java.util.UUID
import javax.inject.Inject
import io.lbert.app.services.ArtistService
import io.lbert.play.controllers._

class ArtistController @Inject()(actionInjector: ActionInjector,
                                 service: ArtistService) {

  import io.lbert.app.models.ArtistJson._
  import actionInjector._

  val artistRestSchema = SchemaREST(artistFormat,searchArtistMapping,"artist")

  def create           = ActionSchema(service.create _)(artistRestSchema.create)
  def get(id: UUID)    = ActionSchema(service.get(id))(artistRestSchema.get)
  def search           = ActionSchema(service.search _)(artistRestSchema.search)
  def update(id: UUID) = ActionSchema(service.update(id) _)(artistRestSchema.update)
  def delete(id: UUID) = ActionSchema(service.delete(id))(artistRestSchema.delete)
}
