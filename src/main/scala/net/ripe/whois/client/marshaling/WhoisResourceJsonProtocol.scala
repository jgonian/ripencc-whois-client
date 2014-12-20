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

  implicit val uriReader = lift {
    new RootJsonReader[URI] {
      override def read(json: JsValue): URI = {
        json.asJsObject().getFields("type", "href") match {
          case Vector(JsString(t), JsString(href)) => new URI(href)
          case _ => deserializationError("URI expected")
        }
      }
    }
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

  implicit val objectReader = lift {
    new RootJsonReader[Object] {
      override def read(json: JsValue): Object = {
        Object(
          objectType = json.asJsObject.getFields("type").head.convertTo[String],
          link = json.asJsObject.getFields("link").head.convertTo[URI],
          primaryKey = json.asJsObject.getFields("primary-key").head.asJsObject().getFields("attribute").head.convertTo[Seq[Attribute]].map(_.value).mkString,
          attributes = json.asJsObject.getFields("attributes").head.asJsObject().getFields("attribute").head.convertTo[Seq[Attribute]]
        )
      }
    }
  }

  implicit object JsArrayWriter extends RootJsonFormat[JsArray] {
    def write(jsArray: JsArray) = jsArray
    def read(value: JsValue) = value.asInstanceOf[JsArray]
  }

  implicit val messageReader = lift {
    new RootJsonReader[Message] {
      override def read(json: JsValue): Message = {

        Message(
          severity = json.asJsObject.getFields("severity").head.convertTo[String],
          textTemplate = json.asJsObject.getFields("text").head.convertTo[String],
          args = None //TODO [TP]: fix parsing Option(json.asJsObject.getFields("args").map(_.convertTo[String]))
        )
      }
    }
  }

  implicit val responseReader = lift {
    new RootJsonReader[WhoisResponse] {
      override def read(json: JsValue): WhoisResponse = {
        WhoisResponse(
          objects = json.asJsObject.getFields("objects").head.asJsObject.getFields("object").head.convertTo[Seq[Object]],
          messages = None //TODO [TP]: should be an option of messages. fix parsing
        )
      }
    }
  }

  implicit val failResponseReader = lift {
    new RootJsonReader[WhoisFailResponse] {
      override def read(json: JsValue): WhoisFailResponse = {
        WhoisFailResponse(
          objects = None, //TODO [TP]: should be an option of objects. fix parsing
          messages = json.asJsObject.getFields("errormessages").head.asJsObject.getFields("errormessage").head.convertTo[Seq[Message]]
        )
      }
    }
  }

}
