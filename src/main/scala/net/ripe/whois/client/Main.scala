package net.ripe.whois.client

import akka.actor.ActorSystem
import akka.dispatch.Futures
import akka.event.Logging
import net.ripe.whois.client.view.WhoisResponse

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


  val whoisClient = new HttpWhoisClient("https://rest-test.db.ripe.net/test")

  val lookup1 = whoisClient.lookup("mntner", "TEST-DBM-MNT")
  val lookup2 = whoisClient.lookup("mntner", "TEST-MNT")

  lookup1 onSuccess { toRpslString }
  lookup2 onSuccess { toRpslString }


  Future.sequence(Seq(lookup1, lookup2)).onComplete {
    case _ =>
      log.info("shutting down...")
      system.shutdown()
  }

}
