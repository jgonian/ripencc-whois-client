package net.ripe.whois.client.marshaling

import net.ripe.whois.client.model.WhoisResource
import spray.json._


trait WhoisResourceJsonProtocol extends DefaultJsonProtocol {

  implicit val whoisResourceFormat = jsonFormat1(WhoisResource)

}
