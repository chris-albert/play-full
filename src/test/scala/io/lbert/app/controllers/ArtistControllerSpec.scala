package io.lbert.app.controllers

import java.util.UUID
import io.lbert.app.models.{Artist, ArtistSearch}
import io.lbert.app.services.ArtistService
import io.lbert.play.util.TestHelper._
import io.lbert.play.util.AsyncTestSpec
import play.api.inject._
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import scala.concurrent.Future


class ArtistControllerSpec extends AsyncTestSpec {

  private val app = GuiceApplicationBuilder()
    .overrides(bind[ArtistService] to new MockArtistService)
    .build()

  private val artist = ArtistFactory.build

  private val artistJson = Json.obj(
    "id"   -> artist.id.toString,
    "name" -> artist.name
  )

  private val wrappedArtistJson = Json.obj(
    "artist" -> artistJson
  )

  def runRestTests(version: String) = {

    s"Artist $version" should {
      "create an artist" in {
        for {
          result <- route(app, FakeRequest("POST", s"/$version/artists").withBody(wrappedArtistJson)).get
          json <- resultToJson(result)
        } yield {
          result.header.status mustBe CREATED
          json mustBe wrappedArtistJson
        }
      }
      "get an artist" in {
        for {
          result <- route(app, FakeRequest("GET", s"/$version/artists/${artist.id}")).get
          json <- resultToJson(result)
        } yield {
          result.header.status mustBe OK
          json mustBe wrappedArtistJson
        }
      }
      "search for artist" in {
        for {
          result <- route(app, FakeRequest("GET", s"/$version/artists")).get
          json <- resultToJson(result)
        } yield {
          result.header.status mustBe OK
          json mustBe Json.obj("artists" -> Json.arr(artistJson, artistJson))
        }
      }
      "update an artist" in {
        for {
          result <- route(app, FakeRequest("PUT", s"/$version/artists/${artist.id}").withBody(wrappedArtistJson)).get
          json <- resultToJson(result)
        } yield {
          result.header.status mustBe OK
          json mustBe wrappedArtistJson
        }
      }
      "delete an artist" in {
        for {
          result <- route(app, FakeRequest("DELETE", s"/$version/artists/${artist.id}")).get
          out <- resultToString(result)
        } yield {
          result.header.status mustBe NO_CONTENT
          out mustBe ""
        }
      }
    }
  }

  runRestTests("v0")
  runRestTests("v1")
  runRestTests("v2")
  runRestTests("v3")

  class MockArtistService extends ArtistService {

    override def create(artist: Artist): Future[Artist] =
      Future.successful(artist)

    override def get(id: UUID): Future[Artist] =
      Future.successful(artist)

    override def search(search: ArtistSearch): Future[Seq[Artist]] =
      Future.successful(Seq(artist,artist))

    override def update(id: UUID)(artist: Artist): Future[Artist] =
      Future.successful(artist)

    override def delete(id: UUID): Future[Unit] =
      Future.successful(())
  }
}

object ArtistFactory {
  def build: Artist =
    Artist(
      id = UUID.randomUUID(),
      name = UUID.randomUUID().toString
    )
}
