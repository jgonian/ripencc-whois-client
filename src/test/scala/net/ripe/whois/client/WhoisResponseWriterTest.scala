package net.ripe.whois.client

import java.net.URI

import net.ripe.whois.client.marshaling.WhoisResourceJsonProtocol
import net.ripe.whois.client.view.{Object, Attribute, Reference, WhoisResponse}
import org.specs2.mutable._
import spray.json._

import scala.io.Source

class WhoisResponseWriterTest extends Specification with WhoisResourceJsonProtocol {

  "Whois response object" should {

    "be serialised into JSON" in {

      val whoisResponseDto = WhoisResponse(Seq(Object(
        objectType = "mntner",
        link = new URI("http://rest-test.db.ripe.net/test/mntner/TEST-DBM-MNT"),
        primaryKey = "TEST-DBM-MNT",
        attributes = Seq(Attribute(
          name = "mntenr",
          value = "TEST-DBM-MNT",
          reference = None,
          comment = None
        ))
      )))

      val expectedJsonFromFile = Source.fromInputStream(getClass.getResourceAsStream("/marshaling/TEST-DBM-MNT-writes.json")).mkString

      whoisResponseDto.toJson shouldEqual JsonParser(expectedJsonFromFile)
    }
  }
}
