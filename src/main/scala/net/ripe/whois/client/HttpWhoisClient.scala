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
      withAuth(authentications) ~>
        addHeader(Accept(MediaTypes.`application/json`)) ~>
        logRequest(log) ~>
        sendReceive ~>
        logResponse(log) ~>
        unmarshal[WhoisResponse]

    pipeline {
      Get(s"$whoisBaseUrl/$objectType/$objectId.json?unfiltered")
    }
  }

  private def withAuth(whoisAuthentications: Seq[WhoisAuthentication]): RequestTransformer = { request: HttpRequest =>

    @tailrec def loop(whoisAuthentications: Seq[WhoisAuthentication], requestFun: RequestTransformer, req: HttpRequest): HttpRequest  = {
      whoisAuthentications match {
        case Nil => requestFun(req)
        case head :: tail =>
          head match {
            case auth: WhoisCrowdAuthentication => loop(tail, requestFun ~> addHeader(Cookie(HttpCookie("crowd.token_key", auth.credentials))), req)
            case auth: WhoisPasswordAuthentication => loop(tail, req => Get(req.uri + "&password=" + auth.credentials), req)
            case auth: WhoisNoAuthentication => loop(tail, requestFun, req)
          }
      }
    }
    loop(whoisAuthentications, (x: HttpRequest) => request, request)
  }

}
