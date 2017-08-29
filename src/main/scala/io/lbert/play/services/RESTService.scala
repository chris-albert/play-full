package io.lbert.play.services

trait RESTService[Res,Id,Search,F[_]] {
  def create(resource: Res): F[Res]
  def get(id: Id): F[Res]
  def update(id: Id)(resource: Res): F[Res]
  def delete(id: Id): F[Unit]
  def search(search: Search): F[Seq[Res]]
}
