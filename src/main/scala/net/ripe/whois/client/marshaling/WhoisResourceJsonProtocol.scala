package net.ripe.whois.client.marshaling

import java.net.URI

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

  implicit val uriFormat = new RootJsonFormat[URI] {
    override def read(json: JsValue): URI = {
      json.asJsObject().getFields("type", "href") match {
        case Vector(JsString(t), JsString(href)) => new URI(href)
        case _ => deserializationError("URI expected")
      }
    }

    override def write(obj: URI): JsValue = JsObject()
  }

  implicit val attributeReader = lift {
    new RootJsonReader[Attribute] {
      override def read(json: JsValue): Attribute = {

        val reference: Option[Reference] = json.asJsObject.fields.get("referenced-type") map {
          value => Reference(value.convertTo[String], json.asJsObject.getFields("link").head.convertTo[URI])
        }

        Attribute(
          name = json.asJsObject.getFields("name").head.convertTo[String],
          value = json.asJsObject.getFields("value").head.convertTo[String],
          reference = reference,
          comment = json.asJsObject.fields.get("comment").map(_.convertTo[String])
        )
      }
    }
  }

  implicit val whoisObjectReader = new RootJsonFormat[Object] {

      override def read(json: JsValue): Object = {
        Object(
          objectType = json.asJsObject.getFields("type").head.convertTo[String],
          link = json.asJsObject.getFields("link").head.convertTo[URI],
          primaryKey = json.asJsObject.getFields("primary-key").head.asJsObject().getFields("attribute").head.convertTo[Seq[Attribute]].map(_.value).mkString,
          attributes = json.asJsObject.getFields("attributes").head.asJsObject().getFields("attribute").head.convertTo[Seq[Attribute]]
        )
      }

      override def write(obj: Object): JsValue = {
        JsObject(
          "type" -> JsString(obj.objectType),
          "link" -> obj.link.toJson,
          "primary-key" -> JsObject(),
          "attributes" -> JsObject()
        )
      }
  }

  implicit val whoisResponseFormat = new RootJsonFormat[WhoisResponse] {

    override def read(json: JsValue): WhoisResponse = {
      WhoisResponse(
        objects = json.asJsObject.getFields("objects").head.asJsObject.getFields("object").head.convertTo[Seq[Object]]
      )
    }

    override def write(obj: WhoisResponse): JsValue = {
      JsObject(
        "objects" -> JsObject(
          "object" -> obj.objects.toJson
        )
      )
    }
  }
}
