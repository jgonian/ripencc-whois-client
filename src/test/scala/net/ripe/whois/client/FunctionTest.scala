package net.ripe.whois.client

import java.net.URI

import net.ripe.whois.client.marshaling.WhoisResourceJsonProtocol
import net.ripe.whois.client.view.{Attribute, Reference, Response}
import org.specs2.mutable._
import spray.json._

import scala.io.Source

class FunctionTest extends Specification with WhoisResourceJsonProtocol {

  "Response from WHOIS" should {

    "be parsed into a Response object" in {

      val testDbmMntJsonFromFile = Source.fromInputStream(getClass.getResourceAsStream("/marshaling/TEST-DBM-MNT.json")).mkString

      val response = JsonParser(testDbmMntJsonFromFile).convertTo[Response]

      response.objects.size shouldEqual 1

      response.objects.head.objectType shouldEqual "mntner"

      response.objects.head.link shouldEqual new URI("http://rest-test.db.ripe.net/test/mntner/TEST-DBM-MNT")

      response.objects.head.primaryKey shouldEqual "TEST-DBM-MNT"

      response.objects.head.attributes.size shouldEqual 12

      response.objects.head.attributes.head shouldEqual Attribute("mntner", "TEST-DBM-MNT", None, None)

      response.objects.head.attributes.drop(2).head shouldEqual Attribute(
        name = "admin-c",
        value = "AA1-TEST",
        reference = Some(Reference("person", new URI("http://rest-test.db.ripe.net/test/person/AA1-TEST"))),
        comment = None)

      response.objects.head.attributes.last shouldEqual Attribute("source", "TEST", None, Some("Filtered"))

    }

    "transform combined key correctly into the Response object" in {
      val testRouteJsonFromFile = Source.fromInputStream(getClass.getResourceAsStream("/marshaling/route.json")).mkString

      val response = JsonParser(testRouteJsonFromFile).convertTo[Response]
      response.objects.head.primaryKey shouldEqual "10.11.11.0/24AS101111"
    }
  }

}
