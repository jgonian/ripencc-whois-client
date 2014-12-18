package net.ripe.whois.client

import net.ripe.whois.client.view.Attribute
import spray.http.HttpResponse

import scala.concurrent.Future
import scala.util.Try

trait WhoisClient {

  def lookup(): Future[HttpResponse] // return Future[WhoisResource]
}
