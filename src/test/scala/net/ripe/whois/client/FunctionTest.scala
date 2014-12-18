package net.ripe.whois.client

import java.net.URI

import net.ripe.whois.client.marshaling.WhoisResourceJsonProtocol
import net.ripe.whois.client.view.{Reference, Attribute, Object, Response}
import org.specs2.mutable._
import spray.json._

import scala.io.Source

class FunctionTest extends Specification with WhoisResourceJsonProtocol {

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
          primaryKey = null,
          attributes = json.asJsObject.getFields("attributes").head.asJsObject().getFields("attribute").head.convertTo[Seq[Attribute]]
        )
      }
    }
  }

  implicit val responseReader = lift {
    new RootJsonReader[Response] {
      override def read(json: JsValue): Response = {
        Response(
          objects = json.asJsObject.getFields("objects").head.asJsObject.getFields("object").head.convertTo[Seq[Object]]
        )
      }
    }
  }


  "Response from WHOIS" should {

    "be parsed into a Response object" in {

      val testDbmMntJsonFromFile = Source.fromInputStream(getClass.getResourceAsStream("/marshaling/TEST-DBM-MNT.json")).mkString

      val response = JsonParser(testDbmMntJsonFromFile).convertTo[Response]

      response.objects.size shouldEqual 1

      response.objects.head.objectType shouldEqual "mntner"

      response.objects.head.link shouldEqual new URI("http://rest-test.db.ripe.net/test/mntner/TEST-DBM-MNT")

      response.objects.head.primaryKey shouldEqual null

      response.objects.head.attributes.size shouldEqual 12

      response.objects.head.attributes.head shouldEqual Attribute("mntner", "TEST-DBM-MNT", None, None)

      response.objects.head.attributes.drop(2).head shouldEqual Attribute(
        name = "admin-c",
        value = "AA1-TEST",
        reference = Some(Reference("person", new URI("http://rest-test.db.ripe.net/test/person/AA1-TEST"))),
        comment = None)

      response.objects.head.attributes.last shouldEqual Attribute("source", "TEST", None, Some("Filtered"))
    }
  }

}
