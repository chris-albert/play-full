package io.lbert.play.util

import org.mockito.Matchers.any
import org.mockito.Mockito.{mock, when}
import play.api.libs.json.JsObject
import scala.concurrent.Future

object TestMocks {

  def auditor: Auditor = {
    val mockAuditor = mock(classOf[Auditor])
    when(mockAuditor.log(any[String], any[JsObject])) thenReturn Future.successful(())
    mockAuditor
  }
}
