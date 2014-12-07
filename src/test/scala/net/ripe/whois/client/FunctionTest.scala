package net.ripe.whois.client

import org.specs2.mutable._

class FunctionTest extends Specification {

  "Booleans" should {
    "handle equality right" in {
      true shouldEqual true
      true shouldNotEqual false
    }
  }

}
