package io.lbert.app.controllers.v3

import java.util.UUID
import javax.inject.Inject
import io.lbert.app.controllers.RESTController
import io.lbert.app.models.ArtistJson.{artistFormat, searchArtistMapping}
import io.lbert.app.models.{Artist, ArtistSearch}
import io.lbert.app.services.ArtistService
import io.lbert.play.controllers.{ActionInjector, SchemaREST}

class ArtistController @Inject()(actionInjector: ActionInjector,
                                 service: ArtistService)
  extends RESTController[Artist,UUID,ArtistSearch](
    actionInjector,
    service,
    SchemaREST(artistFormat,searchArtistMapping,"artist")
  )
