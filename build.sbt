name := "lab2"

version := "0.1"

scalaVersion := "2.13.12"

resolvers += "Akka library repository".at("https://repo.akka.io/maven")

libraryDependencies += "com.typesafe.akka" %% "akka-actor-typed" % "2.8.0"

libraryDependencies +=  "ch.qos.logback" % "logback-classic" % "1.4.7"
