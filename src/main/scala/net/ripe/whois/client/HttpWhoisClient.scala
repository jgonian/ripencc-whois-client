package net.ripe.whois.client

import akka.actor.ActorSystem
import net.ripe.whois.client.marshaling.WhoisResourceJsonProtocol
import net.ripe.whois.client.view.Attribute
import spray.httpx.SprayJsonSupport

import scala.concurrent.{ExecutionContext, Future}
import spray.client.pipelining._
import SprayJsonSupport._


class HttpWhoisClient(implicit as: ActorSystem, ec: ExecutionContext) extends WhoisClient with WhoisResourceJsonProtocol {

  override def lookup(): Future[Attribute] = {

    val pipeline = sendReceive ~> unmarshal[Attribute]

    pipeline {
      Get("http://rest-prepdev.db.ripe.net/ripe/mntner/ygoniana-mnt.json")
    }
  }

}
