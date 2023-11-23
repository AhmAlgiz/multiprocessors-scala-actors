import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}

import java.lang.Thread.sleep
import scala.concurrent.Await

object Node {
  sealed trait Protocol

  case class Greet(replyto: ActorRef[Protocol]) extends Protocol
  case class Greeter() extends Protocol
  case class Decide() extends Protocol
  case class AddFriends(refs: List[ActorRef[Node.Protocol]]) extends Protocol
  case class Counter() extends Protocol

  def behavior(name: String): Behavior[Protocol] = {
    Behaviors.setup { ctx =>

      var friends: List[ActorRef[Node.Protocol]] = List.empty
      var msgCount: Int = 0

      Behaviors.receiveMessage {

        case Greet(replyto) =>
          ctx.log.info("Recieved greet msg, reply to: {}\n", replyto)
          replyto ! Counter()
          Behaviors.same

        case Greeter() =>
          friends.foreach( f =>
          f ! Greet(ctx.self))
          Behaviors.same

        case AddFriends(refs) =>
          friends = refs ::: friends
          ctx.log.info("friends added: {}\n", refs)
          Behaviors.same

        case Decide() =>
          ctx.log.info("Decided. Reset counter.")
          msgCount = 0
          Behaviors.same

        case Counter() =>
          msgCount += 1
          ctx.log.info("Counter incremented: {}", msgCount)
          if (msgCount == friends.length) {
            ctx.self ! Decide()
          }
          Behaviors.same
      }
    }
  }
}

object Main extends App {
  implicit val system: ActorSystem[Node.Protocol] = ActorSystem(Node.behavior("root"), "root")

  println("System started\n")

  val Actor1: ActorRef[Node.Protocol] = system.systemActorOf(Node.behavior("actor1"), "actor1")
  val Actor2: ActorRef[Node.Protocol] = system.systemActorOf(Node.behavior("actor2"), "actor2")
  val Actor3: ActorRef[Node.Protocol] = system.systemActorOf(Node.behavior("actor3"), "actor3")

  Actor1 ! Node.AddFriends(List(Actor2, Actor3))
  Actor2 ! Node.AddFriends(List(Actor1, Actor3))
  Actor3 ! Node.AddFriends(List(Actor1, Actor2))

  Actor1 ! Node.Greeter()
  Actor2 ! Node.Greeter()
  Actor3 ! Node.Greeter()

  sleep(5000)

  system.terminate()
  println("ActorSystem terminated\n")
}

