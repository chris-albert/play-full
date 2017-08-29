package io.lbert.play.controllers

import play.api.data.{Form => PlayForm}
import play.api.libs.json.Reads
import play.api.mvc.{AnyContent, Request}
import scala.concurrent.{ExecutionContext, Future}
import scalaz.ReaderT

object ReaderMonad {

  type ReaderMonad[A] = ReaderT[Future,Dependencies,A]

  def playForm[A](form: PlayForm[A]): ReaderMonad[A] =
    ReaderT(deps =>
      form.bindFromRequest()(deps.request).fold(
        error => Future.failed(UnprocessableEntityError.form(error.errors)),
        good  => Future.successful(good)
      )
    )

  def json[A](reads: Reads[A]): ReaderMonad[A] =
    ReaderT(deps =>
      deps.request.body.asJson
        .map(json => json.validate(reads).fold(
          errors => Future.failed(UnprocessableEntityError.json(errors)),
          a => Future.successful(a)
        ))
        .getOrElse(Future.failed(UnprocessableEntityError(Some("Expecting application/json request body"))))
    )

  case class Dependencies(request: Request[AnyContent])
                         (implicit val executionContext: ExecutionContext)

}
