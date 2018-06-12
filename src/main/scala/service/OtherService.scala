package service

import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class OtherService extends LazyLogging {

  def doOtherThing(): Future[Unit] = {
    Future {
      logger.info("doing other thing")
      Thread.sleep( 1000)
      logger.info("stopped doing other thing")
    }
  }

}
