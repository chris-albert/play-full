package io.lbert.play.util

import akka.actor.Cancellable
import akka.stream.{Attributes, ClosedShape, Graph, Materializer}
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import play.api.test.Helpers._
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag
import scala.util.{Failure, Success, Try}

object TestHelper {

  def inMemoryFakeApp: Application =
    new GuiceApplicationBuilder().build()

  def fakeDbApp: Application = inMemoryFakeApp

  def withFakeApplication[A](fakeApp: Application)
                            (block: Application => A): A =
    running(fakeApp)(block(fakeApp))

  def withFakeApplication[A](block: Application => A): A =
    withFakeApplication(fakeDbApp)(block)

  def withAppAndDb[A](block: Application => A): A =
    block(fakeDbApp)

  def withAppDbService[A: ClassTag,B](block: (Application,A) => B)
                                     (implicit executionContext: ExecutionContext): Future[B] =
    withAsyncAppDbService[A,B]{(app,a) =>
      Try(block(app,a)) match {
        case Success(s) => Future.successful(s)
        case Failure(f) => Future.failed(f)
      }
    }

  def withAsyncAppDbService[A: ClassTag,B](block: (Application,A) => Future[B])
                                          (implicit executionContext: ExecutionContext): Future[B] = {
    val app = fakeDbApp
    val a = app.injector.instanceOf[A]
    block(app,a).flatMap {result =>
      app.stop().map(_ => result)
    }.recoverWith{ case e =>
      app.stop().flatMap(_ => Future.failed(e))
    }
  }

  def buildQueryString(m: Map[String,Any]): String =
    m.map{case (k,v) => s"$k=$v"}.mkString("&")

  def buildUrl(url: String,m: Map[String,Any]): String =
    url + "?" + buildQueryString(m)

  def resultStatus(result: Result): Int = result.header.status

  def resultToJson(result: Result)(implicit executionContext: ExecutionContext): Future[JsValue] =
    resultToString(result).flatMap(r => Try(Json.parse(r)) match {
      case Success(j) => Future.successful(j)
      case Failure(e) => Future.failed(new Exception(s"Failed to parse [$r] as JSON"))
    })

  def resultToString(result: Result)(implicit executionContext: ExecutionContext): Future[String] =
    result.body.consumeData(NoMaterializer).map(_.decodeString("utf-8"))

  object NoMaterializer extends Materializer {
    def withNamePrefix(name: String) = throw new UnsupportedOperationException("NoMaterializer cannot be named")
    implicit def executionContext = throw new UnsupportedOperationException("NoMaterializer does not have an execution context")
    def materialize[Mat](runnable: Graph[ClosedShape, Mat]) =
      throw new UnsupportedOperationException("No materializer was provided, probably when attempting to extract a response body, but that body is a streamed body and so requires a materializer to extract it.")
    override def scheduleOnce(delay: FiniteDuration, task: Runnable): Cancellable =
      throw new UnsupportedOperationException("NoMaterializer can't schedule tasks")
    override def schedulePeriodically(initialDelay: FiniteDuration, interval: FiniteDuration, task: Runnable): Cancellable =
      throw new UnsupportedOperationException("NoMaterializer can't schedule tasks")
    override def materialize[Mat](runnable: Graph[ClosedShape, Mat], initialAttributes: Attributes): Mat =
      throw new UnsupportedOperationException("NoMaterializer can't schedule tasks")
  }
}
