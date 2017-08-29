package io.lbert.app.models

import java.util.UUID
import play.api.data.Forms
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

case class Artist(id: UUID,
                  name: String)

case class ArtistSearch(id: List[UUID],
                        name: List[String])

object ArtistJson {

  val artistFormat: Format[Artist] = (
    (JsPath \ "id").format[UUID] and
      (JsPath \ "name").format[String]
    )(Artist.apply,unlift(Artist.unapply))

  val artistWrites = Writes(artistFormat.writes)

  val artistReads = Reads(artistFormat.reads)

  val searchArtistMapping = Forms.mapping(
    "ids"   -> Forms.list(Forms.uuid),
    "names" -> Forms.list(Forms.text)
  )(ArtistSearch.apply)(ArtistSearch.unapply)

}
