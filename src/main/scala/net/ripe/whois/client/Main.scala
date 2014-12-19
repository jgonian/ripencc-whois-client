package net.ripe.whois.client

import akka.actor.ActorSystem
import akka.event.Logging

import scala.util.{Failure, Success}


object Main extends App {

  implicit val system = ActorSystem("whois-client")
  implicit val executionContext = system.dispatcher
  val log = Logging(system, getClass)
  log.info("starting up...")

  val whoisBaseUrl = "https://rest-test.db.ripe.net/test"

  val whoisClient = new HttpWhoisClient(whoisBaseUrl)
  val authentications = Seq(WhoisCrowdAuthentication("7Okn8y5Vyg8kOwY0tP4yqw00"), WhoisPasswordAuthentication("emptypassword"))

  whoisClient.lookup("mntner", "TEST-DBM-MNT", authentications).onComplete {
    case Success(response) =>
      log.info("shutting down...SUCCESS" + response.objects.head.attributes)
      system.shutdown()
    case Failure(ex) =>
      log.info(ex.getMessage)
      log.info("shutting down...FAILURE")
      system.shutdown()
  }

}
