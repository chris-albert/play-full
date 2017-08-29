package io.lbert.app.controllers.v3

import java.util.UUID
import javax.inject.Inject
import io.lbert.app.controllers.RESTController
import io.lbert.app.models.ArtistJson.{artistFormat, searchArtistMapping}
import io.lbert.app.models.{Artist, ArtistSearch}
import io.lbert.app.services.ArtistService
import io.lbert.play.controllers.{SchemaInjector, SchemaREST}

class ArtistController @Inject()(tFSchemaInjector: SchemaInjector,
                                 service: ArtistService)
  extends RESTController[Artist,UUID,ArtistSearch](
    tFSchemaInjector,
    service,
    SchemaREST(artistFormat,searchArtistMapping,"artist")
  )
