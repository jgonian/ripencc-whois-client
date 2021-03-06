package net.ripe.whois.client

import net.ripe.whois.client.view.WhoisResource

import scala.concurrent.Future
import scala.util.Try

trait WhoisClient {

  def lookup(): Future[WhoisResource] // return Future[WhoisResource]
}
