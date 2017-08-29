package io.lbert.play.controllers

import play.api.mvc.{AnyContent, Request}
import scala.concurrent.Future

trait Authentication[A] {
  def fromRequest(request: Request[AnyContent]): Future[A]
}
