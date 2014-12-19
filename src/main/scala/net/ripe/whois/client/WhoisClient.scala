package net.ripe.whois.client

import net.ripe.whois.client.view.{WhoisResponse, Attribute}
import spray.http.HttpResponse

import scala.concurrent.Future
import scala.util.Try

trait WhoisClient {

  def lookup(objectType: String, objectId: String, authentication: Seq[WhoisAuthentication] = Nil): Future[WhoisResponse]
  def create(objectType: String, whoisResponse: WhoisResponse, authentications: Seq[WhoisAuthentication]): Future[WhoisResponse]
  def update(objectType: String, objectId: String, whoisResponse: WhoisResponse, authentications: Seq[WhoisAuthentication]): Future[WhoisResponse]
  def delete(objectType: String, objectId: String, authentication: Seq[WhoisAuthentication]): Future[WhoisResponse]
}
