import scala.concurrent.duration.DurationInt
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.util.Timeout

object Node {
  sealed trait Protocol

  case class Greet(replyto: ActorRef[Int]) extends Protocol
  case class Greeter() extends Protocol
  case class Decide() extends Protocol
  case class AddFriend(ref: ActorRef[Node.Protocol]) extends Protocol
  case class Init(name: String) extends Protocol

  def behavior(name: String): Behavior[Protocol] = {
    Behaviors.setup { ctx =>

      var friends: List[ActorRef[Node.Protocol]] = List.empty
      var msgCount: Int = 0

      Behaviors.receiveMessage {

        case Greet(replyto) =>
          msgCount += 1
          println("Recieved greet msg, reply to: " + replyto)
          replyto ! msgCount
          Behaviors.same

        case Greeter() =>
          Behaviors.same

        case AddFriend(ref) =>
          friends = ref :: friends
          Behaviors.same

        case Decide() =>
          println("Decided", name)
          Behaviors.same

      }
    }
  }
}

object Main extends App {
  implicit val system: ActorSystem[Node.Protocol] = ActorSystem(Node.behavior("root"), "root")

  println("System started")

  val Actor1: ActorRef[Node.Protocol] = system.systemActorOf(Node.behavior("actor1"), "actor1")
  val Actor2: ActorRef[Node.Protocol] = system.systemActorOf(Node.behavior("actor2"), "actor2")
  val Actor3: ActorRef[Node.Protocol] = system.systemActorOf(Node.behavior("actor3"), "actor3")

  Actor1 ! Node.Decide()




  implicit val timeout: Timeout = 5.seconds

  system.terminate()
  println("ActorSystem terminated")
}

