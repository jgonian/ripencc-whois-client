package net.ripe.whois.client


abstract class WhoisAuthentication(val credentials: String) {
  val noAuthentication = this.isInstanceOf[WhoisNoAuthentication]
  val crowdAuthentication = this.isInstanceOf[WhoisCrowdAuthentication]
  val passwordAuthentication = this.isInstanceOf[WhoisPasswordAuthentication]
}

case class WhoisCrowdAuthentication(crowdToken: String) extends WhoisAuthentication(crowdToken)
case class WhoisPasswordAuthentication(password: String) extends WhoisAuthentication(password)
case class WhoisNoAuthentication() extends WhoisAuthentication(null)
