package net.ripe.whois.client.view

case class Link(linkType: String, href: String)
case class Reference(referencedType: String, link: Link)
case class Attribute(name: String, value: String, reference: Option[Reference], comment: Option[String])