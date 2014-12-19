package net.ripe.whois.client

import akka.actor.ActorSystem
import net.ripe.whois.client.marshaling.WhoisResourceJsonProtocol
import net.ripe.whois.client.view.{Response, Attribute}
import spray.http.HttpHeaders.Accept
import spray.http.{MediaTypes, HttpRequest, HttpResponse}
import spray.httpx.SprayJsonSupport

import scala.concurrent.{ExecutionContext, Future}
import spray.client.pipelining._
import SprayJsonSupport._


class HttpWhoisClient(implicit as: ActorSystem, ec: ExecutionContext) extends WhoisClient with WhoisResourceJsonProtocol {

  override def lookup(objectType: String, objectId: String): Future[Response] = {

    val pipeline: (HttpRequest => Future[Response]) =
      addHeader(Accept(MediaTypes.`application/json`)) ~>
        sendReceive ~>
        unmarshal[Response]

    pipeline {
      Get(s"http://rest-test.db.ripe.net/test/$objectType/$objectId.json")
    }
  }

}
