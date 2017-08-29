package io.lbert.app.controllers

import io.lbert.play.controllers.{RESTSchemas, ActionInjector}
import io.lbert.play.services.RESTService
import play.api.mvc.{Action, AnyContent}
import scala.concurrent.Future

abstract class RESTController[Res,Id,Search](actionInjector: ActionInjector,
                                             service: RESTService[Res,Id,Search,Future],
                                             schema: RESTSchemas[Res,Search]) {

  def create: Action[AnyContent] = actionInjector.ActionSchema(service.create _)(schema.create)
  def get(id: Id): Action[AnyContent] = actionInjector.ActionSchema(service.get(id))(schema.get)
  def search: Action[AnyContent] = actionInjector.ActionSchema(service.search _)(schema.search)
  def update(id: Id): Action[AnyContent] = actionInjector.ActionSchema(service.update(id) _)(schema.update)
  def delete(id: Id): Action[AnyContent] = actionInjector.ActionSchema(service.delete(id))(schema.delete)
}
