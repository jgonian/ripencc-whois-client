package net.ripe.whois.client.view

import java.net.URI

case class Reference(referencedType: String, link: URI)
case class Attribute(name: String, value: String, reference: Option[Reference], comment: Option[String])

case class Object(objectType: String, link: URI, primaryKey: String, attributes: Seq[Attribute]) {

  def findAttribute(name: String) = attributes.find(_.name == name)

  def getAttribute(name: String) = findAttribute(name).getOrElse(sys.error(s"could not fine attribute with name: $name"))

  def isAuthenticated = getAttribute("source").comment.isEmpty

}

case class WhoisResponse(objects: Seq[Object])
