import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors

object Node {
  sealed trait Protocol

  case class Get(replyto: ActorRef[Long]) extends Protocol

  def behavior: Behavior[Protocol] = Behaviors.setup { ctx =>

    var msgCount: Long = 0

    Behaviors.receiveMessage {

      case Get(replyto) =>
        msgCount += 1
        replyto ! msgCount
        Behaviors.same
    }
  }
}
