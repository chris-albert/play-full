package io.lbert.app.controllers

import io.lbert.play.controllers.{RESTSchemas, SchemaInjector}
import io.lbert.play.services.RESTService
import play.api.mvc.{Action, AnyContent}
import scala.concurrent.Future

abstract class RESTController[Res,Id,Search](tFSchemaInjector: SchemaInjector,
                                             service: RESTService[Res,Id,Search,Future],
                                             schema: RESTSchemas[Res,Search]) {

  def create: Action[AnyContent] = tFSchemaInjector.Schema(service.create _)(schema.create)
  def get(id: Id): Action[AnyContent] = tFSchemaInjector.Schema(service.get(id))(schema.get)
  def search: Action[AnyContent] = tFSchemaInjector.Schema(service.search _)(schema.search)
  def update(id: Id): Action[AnyContent] = tFSchemaInjector.Schema(service.update(id) _)(schema.update)
  def delete(id: Id): Action[AnyContent] = tFSchemaInjector.Schema(service.delete(id))(schema.delete)
}
