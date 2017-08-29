package io.lbert.play.controllers

import java.util.UUID
import play.api.data.FormError
import play.api.data.validation.ValidationError
import play.api.http.Status._
import play.api.libs.json._

class ErrorResponse(val statusCode: Int,
                    message: Option[String] = None,
                    fields: JsArray = Json.arr()) extends Exception {

  lazy val id: UUID = UUID.randomUUID()

  lazy val json: JsObject = Json.obj(
    "errors" -> Json.arr(
      Json.obj(
        "id"         -> id.toString,
        "message"    -> message,
        "statusCode" -> statusCode.toString,
        "fields"     -> fields
      )
    )
  )
}

case object NotFoundError extends ErrorResponse(NOT_FOUND)

case class ForbiddenResponse(message: Option[String] = None)
  extends ErrorResponse(FORBIDDEN, Some("Forbidden" + message.map(": " + _).getOrElse("")))

case class UnauthorizedResponse(permission: String)
  extends ErrorResponse(UNAUTHORIZED, Some(s"Permission [$permission] denied"))

case class UnprocessableEntityError(message: Option[String] = Some("Validation Error"),
                                    fields: JsArray = JsArray())
  extends ErrorResponse(UNPROCESSABLE_ENTITY, message, fields)

object UnprocessableEntityError {

  val formErrorsWrites = new Writes[Seq[FormError]] {
    override def writes(errors: Seq[FormError]): JsValue =
      Json.toJson(errors.map(error => error.key -> Json.toJson(error.messages)).toMap)
  }

  def form(formErrors: Seq[FormError]): UnprocessableEntityError =
    UnprocessableEntityError(fields = formErrorsWrites.writes(formErrors).as[JsArray])

  def json(jsonErrors: Seq[(JsPath,Seq[ValidationError])]): UnprocessableEntityError =
    UnprocessableEntityError(fields = JsArray())
}
