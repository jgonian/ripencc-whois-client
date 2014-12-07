package net.ripe.whois.client.marshaling

import net.ripe.whois.client.view._
import spray.json._

trait WhoisResourceJsonProtocol extends DefaultJsonProtocol {

  implicit class PimpedJsObject(obj: JsObject) {

    def readMandatoryFieldAs[A :JsonReader](fieldName: String): A = {
      obj.readFieldAs[A](fieldName)
        .getOrElse(deserializationError("Object is missing required member '" + fieldName + "'"))
    }

    def readFieldAs[A :JsonReader](fieldName: String): Option[A]= {
      obj.fields.get(fieldName).map(_.convertTo[A])
    }

    def extractFromFields[A :JsonReader](fieldNames: String*): Option[A] = {
      if (fieldNames.forall(obj.fields.contains)) Some(obj.convertTo[A])
      else None
    }
  }
  
  implicit class PimpedJsValue(value: JsValue) {

    def withJSObject[A](block: JsObject => A): A = {
      block(value.asJsObject)
    }
  }

  implicit val linkFormat = jsonFormat(Link, "xlink:type", "xlink:href")
  implicit val referenceFormat = jsonFormat(Reference, "link", "referenced-type")
  implicit val attributeReader = new RootJsonReader[Attribute] {
    override def read(json: JsValue): Attribute = {
      json.withJSObject { obj =>
        Attribute(
          name = obj.readMandatoryFieldAs[String]("name"),
          value = obj.readMandatoryFieldAs[String]("value"),
          reference = obj.extractFromFields[Reference]("link", "referenced-type"),
          comment = obj.readFieldAs[String]("comment")
        )  
      }
    }
  }

}
