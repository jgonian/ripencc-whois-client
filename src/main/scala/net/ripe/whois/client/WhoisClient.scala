package net.ripe.whois.client

import net.ripe.whois.client.view.Attribute

import scala.concurrent.Future
import scala.util.Try

trait WhoisClient {

  def lookup(): Future[Attribute] // return Future[WhoisResource]
}
