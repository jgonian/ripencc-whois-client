package net.ripe.whois.client

import akka.actor.ActorSystem
import akka.event.Logging

import scala.util.{Failure, Success}


object Main extends App {

  implicit val system = ActorSystem("whois-client")
  implicit val executionContext = system.dispatcher
  val log = Logging(system, getClass)

  log.info("starting up...")

  val whoisClient = new HttpWhoisClient()

  whoisClient.lookup().onComplete {
    case Success(response) =>
      log.info(response.toString)
      log.info("shutting down...SUCCESS")
      system.shutdown()
    case Failure(ex) =>
      log.info(ex.getMessage)
      log.info("shutting down...FAILURE")
      system.shutdown()
  }



}
