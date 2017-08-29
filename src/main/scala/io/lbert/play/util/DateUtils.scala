package io.lbert.play.util

import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import play.api.libs.json.{JsString, Writes, Reads}

object DateUtils {
  val dateTimeFormatter = ISODateTimeFormat.dateTimeNoMillis().withZoneUTC()

  object Implicits {
    implicit val dateTimeJsonReads = Reads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ssZ")

    implicit val dateTimeJsonWrites: Writes[DateTime] = new Writes[DateTime] {
      def writes(d: DateTime): JsString = JsString(d.toString(dateTimeFormatter))
    }
  }
}

