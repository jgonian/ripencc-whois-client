package net.ripe.whois.client.view

case class Link(linkType: String, href: String)
case class Reference(referencedType: String, link: Link)
case class Attribute(name: String, value: String, reference: Option[Reference], comment: Option[String])
case class Object(objectType: String, link: Link, source: String, primaryKey: Attribute, attributes: Set[Attribute])
case class Response(objects: Set[Object], termsAndConditions: Link)