package io.lbert.play.controllers

import java.util.UUID
import javax.inject.Inject
import io.lbert.play.controllers.ReaderMonad.Dependencies
import io.lbert.play.controllers.SchemaWrites.JsResult
import io.lbert.play.util.Auditor
import play.api.http.Status._
import play.api.Configuration
import play.api.http.Writeable
import play.api.libs.json.{JsValue, _}
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}

class SchemaInjector @Inject()(auditor: Auditor,
                               configuration: Configuration)
                              (implicit ec: ExecutionContext) {
  import SchemaInjector._

  object Schema {

    def apply[A,B](block: A => Future[B])
                  (implicit schemaIO: Schema[A,B]): Action[AnyContent] =
      blockToAction(request => schemaIO.run(buildDependencies(request),block))

    def apply[A](block: Future[A])
                (implicit schemaIO: Schema[Unit,A]): Action[AnyContent] =
      apply[Unit,A](_ => block)
  }

  def blockToAction(block: Request[AnyContent] => Future[JsResult]): Action[AnyContent] =
    Action.async { request =>
      val startTime = System.nanoTime()
      block(request).flatMap { case (result,json) =>
        audit("GoodResponse", request, jsonizeResponse(result, json), startTime)
          .map(_ => render(result,json))
      }.recoverWith {
        case er: ErrorResponse =>
          audit("UnhandledError", request, er.json, startTime)
            .map(_ => new Results.Status(er.statusCode)(er.json))
        case e: Exception =>
          audit("UnhandledError", request, JsString(e.toString), startTime)
            .map(_ => new Results.Status(BAD_REQUEST))
      }
    }

  def audit[A](key: String,
               request: Request[A],
               response: JsValue,
               startTime: Long): Future[Unit] = {
    val id = request.headers.get("TicketFly-Trace-ID").getOrElse(UUID.randomUUID().toString)
    auditor.log(key, Json.obj(
      "id"                -> id,
      "request"           -> formatRequest(request),
      "response"          -> response,
      "elapsedTimeMillis" -> ((System.nanoTime() - startTime) / 1000000)
    ))
  }

  private def render(result: Result, json: Option[JsValue]): Result =
    json.fold(result)(j => result.copy(body = implicitly[Writeable[JsValue]].toEntity(j)))

  private def buildDependencies(request: Request[AnyContent]): Dependencies =
    Dependencies(request)
}

object SchemaInjector {
  def jsonizeResponse(result: Result, json: Option[JsValue]): JsValue = {
    Json.obj(
      "statusCode" -> result.header.status,
      "body"       -> json
    )
  }

  def formatRequest[A](r: Request[A]): JsObject = Json.obj(
    "method" -> r.method,
    "path" -> r.path,
    "query" -> {
      if (r.queryString.isEmpty || r.queryString.headOption.map(_._1 == "").getOrElse(true)) {
        Json.obj()
      } else {
        Json.toJson(r.queryString)
      }
    },
    "body" -> Json.toJson(r.body match {
      case AnyContentAsEmpty => JsNull
      case j: JsValue => j
      case AnyContentAsJson(j) => j
      case any => JsString(any.toString)
    })
  )
}
