import akka.actor.typed.SpawnProtocol.Spawn

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.DurationInt
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, Props, SpawnProtocol}
import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout

object Main extends App {
  implicit val system: ActorSystem[Node.Protocol] = ActorSystem(Node.behavior, "root")

  Console.print("System started\n")

  implicit val timeout: Timeout = 5.seconds

  val nodes: Array[ActorRef[Node.Protocol]] = Array(
    system.systemActorOf(Node.behavior, "node1"),
    system.systemActorOf(Node.behavior, "node2"),
    system.systemActorOf(Node.behavior, "node3")
  )



  system.terminate()
  println("ActorSystem terminated")
}

