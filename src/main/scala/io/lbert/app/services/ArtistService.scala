package io.lbert.app.services

import java.util.UUID
import com.google.inject.ImplementedBy
import io.lbert.app.models.{Artist, ArtistSearch}
import io.lbert.play.services.RESTService
import scala.concurrent.Future

@ImplementedBy(classOf[ArtistServiceImpl])
trait ArtistService extends RESTService[Artist, UUID, ArtistSearch, Future]

class ArtistServiceImpl extends ArtistService {
  override def create(resource: Artist): Future[Artist] = ???

  override def get(id: UUID): Future[Artist] = ???

  override def update(id: UUID)(resource: Artist): Future[Artist] = ???

  override def delete(id: UUID): Future[Unit] = ???

  override def search(search: ArtistSearch): Future[Seq[Artist]] = ???
}
