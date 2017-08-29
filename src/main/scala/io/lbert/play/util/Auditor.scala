package io.lbert.play.util

import com.google.inject.ImplementedBy
import org.joda.time.DateTime
import play.api.Logger
import play.api.libs.json.{Json, JsObject}
import scala.concurrent.Future
import io.lbert.play.util.DateUtils.Implicits._

@ImplementedBy(classOf[AuditLogger])
trait Auditor {

  def auditable: Auditable

  def log(command: String,s: String): Future[Unit] = {
    auditable.log(s"Audit.$command: $s")
  }

  def log[A](command: String,js: JsObject): Future[Unit] = {
    log(command,(js ++ Json.obj(
      "dateTime" -> Json.toJson(new DateTime())
    )).toString)
  }

}

class AuditLogger extends Auditor {
  val logger = Logger("auditor")
  override def auditable: Auditable = new Auditable {
    override def log(s: String): Future[Unit] = {
      logger.info(s)
      Future.successful(())
    }
  }
}

trait Auditable {
  def log(s: String): Future[Unit]
}


