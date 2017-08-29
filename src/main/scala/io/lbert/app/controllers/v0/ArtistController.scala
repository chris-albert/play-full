package io.lbert.app.controllers.v0

import java.util.UUID
import javax.inject.Inject
import io.lbert.app.models.Artist
import io.lbert.app.services.ArtistService
import play.api.mvc.Action
import scala.concurrent.{ExecutionContext, Future}
import play.api.mvc.Results._
import io.lbert.app.models.ArtistJson._
import play.api.data.Form
import play.api.libs.json.{Json, Reads, Writes}

class ArtistController @Inject()(service: ArtistService)
                                (implicit ec: ExecutionContext) {

  def create = Action.async { request =>
    request.body.asJson.map(_.validate(artistReadsWrapped).fold(
      errors => Future.successful(UnprocessableEntity(errors.toString)),
      artist => service.create(artist).map(a => Created(artistWriteWrapped.writes(a)))
    )).getOrElse(Future.successful(UnprocessableEntity("Expecting application/json request body")))
  }

  def get(id: UUID) = Action.async { request =>
    service.get(id).map(a => Ok(artistWriteWrapped.writes(a)))
  }

  def search = Action.async { request =>
    Form(searchArtistMapping).bindFromRequest()(request).fold(
      error => Future.successful(UnprocessableEntity(error.errors.toString)),
      good => service.search(good).map(as => Ok(
        Json.obj("artists" ->
          Json.toJson(as.map(a => artistWrites.writes(a)))
        )))
    )
  }

  def update(id: UUID) = Action.async { request =>
    request.body.asJson.map(_.validate(artistReadsWrapped).fold(
      errors => Future.successful(UnprocessableEntity(errors.toString)),
      artist => service.update(id)(artist).map(a => Ok(artistWriteWrapped.writes(a)))
    )).getOrElse(Future.successful(UnprocessableEntity("Expecting application/json request body")))
  }

  def delete(id: UUID) = Action.async { request =>
    service.delete(id).map(_ => NoContent)
  }

  val artistReadsWrapped = Reads[Artist](r => (r \ "artist").validate[Artist](artistReads))
  val artistWriteWrapped = Writes[Artist](a => Json.obj("artist" -> Json.toJson(a)(artistWrites)))
}
