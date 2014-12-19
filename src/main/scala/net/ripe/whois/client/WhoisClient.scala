package net.ripe.whois.client

import net.ripe.whois.client.view.{Response, Attribute}
import spray.http.HttpResponse

import scala.concurrent.Future
import scala.util.Try

trait WhoisClient {

  def lookup(): Future[Response] // return Future[WhoisResource]
}
