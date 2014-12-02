package net.ripe.whois.client

import akka.actor.ActorSystem
import akka.event.Logging


object Main extends App {

  implicit val system = ActorSystem("whois-client")
  implicit val executionContext = system.dispatcher
  val log = Logging(system, getClass)

  log.info("starting up...")

  val whoisClient = new HttpWhoisClient()

  log.info("shutting down...")
  system.shutdown()

}
