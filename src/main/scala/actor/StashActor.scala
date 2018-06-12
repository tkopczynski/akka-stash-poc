package actor

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorLogging, ActorSystem, Props, Stash}
import akka.actor.Status.Failure
import akka.pattern.pipe
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import service.SomeService

import scala.concurrent.ExecutionContext.Implicits.global

case object DoSomething

case class WrappedResult(result: Int)

class StashActor extends Actor with Stash with ActorLogging {

  private val service = new SomeService

  override def receive: Receive = {
    case DoSomething => {
      log.info("received DoSomething")
      service.doSomething.map(value => WrappedResult(value)) pipeTo self

      context.become({
        case WrappedResult(result) =>
          log.info(s"got $result")
          unstashAll()
          context.unbecome()
        case Failure(ex) =>
          log.info(s"received error $ex")
          unstashAll()
          context.unbecome()
        case DoSomething =>
          log.info("Received DoSomething in additional behaviour context")
          stash()
      }, discardOld = false)
    }
  }
}

object StashActor extends App with LazyLogging {

  implicit val system = ActorSystem("StashActorSystem")

  implicit val timeout = Timeout(5, TimeUnit.SECONDS)
  val localActor = system.actorOf(Props[StashActor], name = "StashActor")

  1.to(10).foreach { _ =>
    Thread.sleep(1000)
    logger.info("sending message")
    localActor ! DoSomething
  }


}
