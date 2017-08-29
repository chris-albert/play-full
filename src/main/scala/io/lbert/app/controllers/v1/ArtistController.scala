package io.lbert.app.controllers.v1

import java.util.UUID
import javax.inject.Inject
import io.lbert.app.services.ArtistService
import io.lbert.play.controllers._
import scala.concurrent.ExecutionContext

class ArtistController @Inject()(actionInjector: ActionInjector,
                                 service: ArtistService)
                                (implicit ec: ExecutionContext) {

  import ArtistSchemas._
  import actionInjector._

  def create           = ActionSchema(service.create _)(createArtistSchema)
  def get(id: UUID)    = ActionSchema(service.get(id))(getArtistSchema)
  def search           = ActionSchema(service.search _)(searchArtistSchema)
  def update(id: UUID) = ActionSchema(service.update(id) _)(updateArtistSchema)
  def delete(id: UUID) = ActionSchema(service.delete(id))(deleteArtistSchema)
}

object ArtistSchemas {

  import io.lbert.app.models.ArtistJson._

  //Create Artist

  val createArtistSchema =
    Schema(
      reads = SchemaReads()
        .JSON(artistReads).withKey("artist"),
      writes = SchemaWrites()
        .JSON(artistWrites).withKey("artist")
        .Status.Created
    )

  //Get Artist

  val getArtistSchema =
    Schema(
      reads = SchemaReads(),
      writes = SchemaWrites()
        .JSON(artistWrites).withKey("artist")
    )

  //Search Artist

  val searchArtistSchema =
    Schema(
      reads = SchemaReads()
        .Form(searchArtistMapping),
      writes = SchemaWrites()
        .JSON(artistWrites).withKey("artists").sequence()
    )

  //Update Artist

  val updateArtistSchema =
    Schema(
      reads = SchemaReads()
        .JSON(artistReads).withKey("artist"),
      writes = SchemaWrites()
        .JSON(artistWrites).withKey("artist")
    )

  //Delete Artist

  val deleteArtistSchema =
    Schema(
      reads = SchemaReads(),
      writes = SchemaWrites().Status.NoContent
    )
}
