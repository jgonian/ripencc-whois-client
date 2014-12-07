name := "ripencc-whois-client"

version := "1.0"

scalaVersion := "2.11.4"

libraryDependencies ++= {
  val akkaVersion = "2.3.7"
  val sprayVersion = "1.3.2"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "io.spray" %% "spray-client" % sprayVersion,
    "io.spray" %% "spray-json" % "1.3.1",
    "ch.qos.logback" % "logback-classic" % "1.0.12",
    "joda-time" % "joda-time" % "2.0",
    // Test dependencies
    "org.specs2" %% "specs2" % "2.3.13" % "test"
  )
}
