package net.ripe.whois.client

import akka.actor.ActorSystem
import net.ripe.whois.client.marshaling.WhoisResourceJsonProtocol
import net.ripe.whois.client.view.Attribute
import spray.http.{HttpRequest, HttpResponse}
import spray.httpx.SprayJsonSupport

import scala.concurrent.{ExecutionContext, Future}
import spray.client.pipelining._
import SprayJsonSupport._


class HttpWhoisClient(implicit as: ActorSystem, ec: ExecutionContext) extends WhoisClient with WhoisResourceJsonProtocol {

  override def lookup(): Future[HttpResponse] = {

    val pipeline: (HttpRequest => Future[HttpResponse]) = sendReceive

    pipeline {
      Get("http://rest-test.db.ripe.net/test/mntner/TEST-DBM-MNT.json")
    }
  }

}
