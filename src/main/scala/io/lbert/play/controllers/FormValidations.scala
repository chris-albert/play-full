package io.lbert.play.controllers

import play.api.data.{FieldMapping, FormError, Forms}
import play.api.data.format.Formatter

object FormValidations {

  def formMapping[A](validateFunc: (String, String) => Either[Seq[FormError], A])
                    (renderFunc: A => String): FieldMapping[A] = {
    val formFieldMapping = new FormFieldMapping[A] {
      override def validate(key: String, field: String): Either[Seq[FormError], A] = validateFunc(key, field)

      override def render(a: A): String = renderFunc(a)
    }
    Forms.of[A](formFieldMapping)
  }

  trait FormFieldMapping[A] extends Formatter[A] {

    def validate(key: String, field: String): Either[Seq[FormError], A]

    def render(a: A): String

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], A] =
      data.get(key) match {
        case Some(field) => validate(key, field)
        case None => Left(Seq(FormError(key, "error.required")))
      }

    override def unbind(key: String, value: A): Map[String, String] =
      Map(key -> render(value))
  }

}
