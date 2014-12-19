package net.ripe.whois.client

import net.ripe.whois.client.view.{WhoisResponse, Attribute}
import spray.http.HttpResponse

import scala.concurrent.Future
import scala.util.Try

trait WhoisClient {

  def lookup(objectType: String, objectId: String): Future[WhoisResponse]
}
