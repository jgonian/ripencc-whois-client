package net.ripe.whois.client

import akka.actor.ActorSystem
import akka.event.Logging
import net.ripe.whois.client.marshaling.WhoisResourceJsonProtocol
import net.ripe.whois.client.view.WhoisResponse
import spray.client.pipelining._
import spray.http.HttpHeaders.{Accept, Cookie}
import spray.http.{HttpCookie, HttpRequest, MediaTypes}
import spray.httpx.SprayJsonSupport._

import scala.annotation.tailrec
import scala.concurrent.{ExecutionContext, Future}


class HttpWhoisClient(val whoisBaseUrl: String)
                     (implicit as: ActorSystem, ec: ExecutionContext) extends WhoisClient with WhoisResourceJsonProtocol {

  val log = Logging(as, getClass)

  override def lookup(objectType: String, objectId: String, authentications: Seq[WhoisAuthentication]): Future[WhoisResponse] = {

    val pipeline: (HttpRequest => Future[WhoisResponse]) =
      addHeader(Accept(MediaTypes.`application/json`)) ~>
        maybeWithCrowdAuth(authentications) ~>
        withAuth(authentications) ~>
        logRequest(log, Logging.InfoLevel) ~>
        sendReceive ~>
        logResponse(log, Logging.InfoLevel) ~>
        unmarshal[WhoisResponse]

    pipeline {
      Get(s"$whoisBaseUrl/$objectType/$objectId.json?unfiltered")
    }
  }

  private def withAuth(whoisAuthentications: Seq[WhoisAuthentication]): RequestTransformer = { request: HttpRequest =>

    @tailrec
    def loop(whoisAuthentications: Seq[WhoisAuthentication], requestFun: RequestTransformer): HttpRequest  = {
      whoisAuthentications match {
        case Nil => request
        case head :: tail =>
          head match {
            case auth: WhoisCrowdAuthentication => loop(tail, requestFun ~> addHeader(Cookie(HttpCookie("crowd.token_key", auth.credentials))))
            case auth: WhoisPasswordAuthentication => loop(tail, req => Get(req.uri + "&password=" + auth.credentials))
            case auth: WhoisNoAuthentication => loop(tail, requestFun)
          }
      }
    }

    loop(whoisAuthentications, (x: HttpRequest) => request)
  }

  private def maybeWithCrowdAuth(authentication: Seq[WhoisAuthentication]): RequestTransformer = { request =>
    authentication.find(_.crowdAuthentication).fold(request) { crowdAuth =>
      request ~> addHeader(Cookie(HttpCookie("crowd.token_key", crowdAuth.credentials)))
    }
  }
}
