package net.ripe.whois.client

import akka.actor.ActorSystem
import net.ripe.whois.client.marshaling.WhoisResourceJsonProtocol
import net.ripe.whois.client.view.{WhoisResponse, Attribute}
import spray.http.HttpHeaders.Accept
import spray.http.{MediaTypes, HttpRequest, HttpResponse}
import spray.httpx.SprayJsonSupport

import scala.concurrent.{ExecutionContext, Future}
import spray.client.pipelining._
import SprayJsonSupport._


class HttpWhoisClient(val whoisBaseUrl: String)
                     (implicit as: ActorSystem, ec: ExecutionContext) extends WhoisClient with WhoisResourceJsonProtocol {

  override def lookup(objectType: String, objectId: String): Future[WhoisResponse] = {

    val pipeline: (HttpRequest => Future[WhoisResponse]) =
      addHeader(Accept(MediaTypes.`application/json`)) ~>
        sendReceive ~>
        unmarshal[WhoisResponse]

    pipeline {
      Get(s"$whoisBaseUrl/$objectType/$objectId.json")
    }
  }

}
