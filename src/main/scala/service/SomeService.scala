package service

import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Random

class SomeService extends LazyLogging {

  private val otherService = new OtherService

  def doSomething: Future[Int] = {
    for {
      _ <- otherService.doOtherThing()
    } yield {
      logger.info("starting doing work")
      Thread.sleep(2000)
      logger.info("stopping doing work")
      if (Random.nextBoolean()) {
        throw new RuntimeException("error")
      }

      1
    }
  }

}
