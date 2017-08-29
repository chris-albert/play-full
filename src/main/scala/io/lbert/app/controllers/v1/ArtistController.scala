package io.lbert.app.controllers.v1

import java.util.UUID
import javax.inject.Inject
import io.lbert.app.services.ArtistService
import io.lbert.play.controllers._
import scala.concurrent.ExecutionContext

class ArtistController @Inject()(tFSchemaIOInjector: SchemaInjector,
                                 service: ArtistService)
                                (implicit ec: ExecutionContext) {

  import ArtistSchemas._
  import tFSchemaIOInjector._

  def create           = Schema(service.create _)(createArtistSchemaIO)
  def get(id: UUID)    = Schema(service.get(id))(getArtistSchemaIO)
  def search           = Schema(service.search _)(searchArtistSchemaIO)
  def update(id: UUID) = Schema(service.update(id) _)(updateArtistSchemaIO)
  def delete(id: UUID) = Schema(service.delete(id))(deleteArtistSchemaIO)
}

object ArtistSchemas {

  import io.lbert.app.models.ArtistJson._

  //Create Artist

  val createArtistSchemaIO =
    Schema(
      reads = SchemaReads()
        .JSON(artistReads).withKey("artist"),
      writes = SchemaWrites()
        .JSON(artistWrites).withKey("artist")
        .Status.Created
    )

  //Get Artist

  val getArtistSchemaIO =
    Schema(
      reads = SchemaReads(),
      writes = SchemaWrites()
        .JSON(artistWrites).withKey("artist")
    )

  //Search Artist

  val searchArtistSchemaIO =
    Schema(
      reads = SchemaReads()
        .Form(searchArtistMapping),
      writes = SchemaWrites()
        .JSON(artistWrites).withKey("artists").sequence()
    )

  //Update Artist

  val updateArtistSchemaIO =
    Schema(
      reads = SchemaReads()
        .JSON(artistReads).withKey("artist"),
      writes = SchemaWrites()
        .JSON(artistWrites).withKey("artist")
    )

  //Delete Artist

  val deleteArtistSchemaIO =
    Schema(
      reads = SchemaReads(),
      writes = SchemaWrites().Status.NoContent
    )
}
