package io.lbert.play.util

import org.joda.time.DateTime
import play.api.libs.json.JsString

class DateUtilSpec extends AsyncTestSpec {

  import DateUtils.Implicits.dateTimeJsonWrites

  "DateTimeSerialization" should {
    "write in expected format" in {
      val date : DateTime = new DateTime("2016-05-24T12:00:00.00-08:00")
      val result = dateTimeJsonWrites.writes(date)
      result mustBe JsString("2016-05-24T20:00:00Z")
    }
  }
}
