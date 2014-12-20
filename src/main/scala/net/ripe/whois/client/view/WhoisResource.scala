package net.ripe.whois.client.view

import java.net.URI

case class Reference(referencedType: String, link: URI)
case class Attribute(name: String, value: String, reference: Option[Reference], comment: Option[String])
case class Object(objectType: String, link: URI, primaryKey: String, attributes: Seq[Attribute])
case class Message(severity: String, textTemplate:String, args:Option[Seq[String]])
case class WhoisResponse(objects: Seq[Object], messages: Option[Seq[Message]])
case class WhoisFailResponse(objects: Option[Seq[Object]], messages: Seq[Message])
//TODO [TP] : make severity like enum.
