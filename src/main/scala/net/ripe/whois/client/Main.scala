package net.ripe.whois.client

import java.net.URI

import akka.actor.ActorSystem
import akka.dispatch.Futures
import akka.event.Logging
import net.ripe.whois.client.WhoisAuthentication
import net.ripe.whois.client.view.{Attribute, Object, WhoisResponse}

import scala.concurrent.Future
import scala.util.{Failure, Success}


object Main extends App {

  implicit val system = ActorSystem("whois-client")
  implicit val executionContext = system.dispatcher
  val log = Logging(system, getClass)
  log.info("starting up...")

  val toRpslString: PartialFunction[WhoisResponse, WhoisResponse] = {
    case response =>
      val whoisObject = response.objects.head
      println(whoisObject.toRpslString)
      println(s"is ${whoisObject.primaryKey} authenticated? ${response.objects.head.isAuthenticated}")
      println()
      response
  }

  val whoisDto = WhoisResponse(Seq(Object(
    objectType = "mntner",
    link = new URI("http://rest-test.db.ripe.net/test/mntner/TEST-DBM-MNT"),
    primaryKey = "TEST-DBM-MNT2",
    attributes = Seq(Attribute(
      name = "mntenr",
      value = "TEST-DBM-MNT2",
      reference = None,
      comment = None
    ))
  )))


  val whoisClient = new HttpWhoisClient("https://rest-test.db.ripe.net/test")

  val lookup1 = whoisClient.lookup("mntner", "TEST-DBM-MNT")
  val lookup2 = whoisClient.lookup("mntner", "TEST-MNT")
  val delete = whoisClient.delete("mntner", "TEST-MNT", Seq(WhoisPasswordAuthentication("testssomnt_wrong")))
  val create = whoisClient.create("mntner", whoisDto, Seq(WhoisPasswordAuthentication("testssomnt")))

  lookup1 onSuccess { toRpslString }
  lookup2 onSuccess { toRpslString }
  delete  onSuccess { toRpslString }
  delete  onFailure { case e => println(e.getMessage) }
  create  onSuccess { toRpslString }
  create  onFailure { case e => println(e.getMessage) }

  Future.sequence(Seq(
    lookup1,
    lookup2,
    delete,
    create
  )).onComplete {
    case _ =>
      log.info("shutting down...")
      system.shutdown()
  }

}

