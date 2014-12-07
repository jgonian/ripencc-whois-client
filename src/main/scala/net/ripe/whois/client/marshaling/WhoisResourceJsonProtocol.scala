package net.ripe.whois.client.marshaling

import net.ripe.whois.client.view._
import spray.json._

trait WhoisResourceJsonProtocol extends DefaultJsonProtocol {

  implicit class PimpedJsObject(obj: JsObject) {

    def readFieldAs[A :JsonReader](fieldName: String): A = {
      obj
        .readOptionalFieldAs[A](fieldName)
        .getOrElse(deserializationError("Object is missing required member '" + fieldName + "'"))
    }

    def readOptionalFieldAs[A :JsonReader](fieldName: String): Option[A]= {
      obj
        .fields
        .get(fieldName)
        .map(_.convertTo[A])
    }

    def readIfFieldPresent[A :JsonReader](fieldName: String): Option[A] = {
      if (obj.fields.contains(fieldName)) Some(obj.convertTo[A])
      else None
    }
  }

  implicit val linkFormat = jsonFormat(Link, "xlink:type", "xlink:href")
  implicit val referenceFormat = jsonFormat(Reference, "link", "referenced-type")
  implicit val attributeReader = new RootJsonReader[Attribute] {
    override def read(json: JsValue): Attribute = {
      val obj = json.asJsObject
      Attribute(
        name = obj.readFieldAs[String]("name"),
        value = obj.readFieldAs[String]("value"),
        reference = obj.readIfFieldPresent[Reference]("link"),
        comment = obj.readOptionalFieldAs[String]("comment")
      )
    }
  }

}
