import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.model.HttpResponse
import akka.stream.Materializer
import akka.stream.scaladsl.FileIO

import java.nio.file.{Paths, StandardOpenOption}
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

object FileSystemActor {
  sealed trait Message
  case class SaveImage(url: String, response: HttpResponse) extends Message

  def apply()(implicit ec: ExecutionContext, mat: Materializer): Behavior[Message] = Behaviors.receive { (context, message) =>
    message match {
      case SaveImage(url, response) =>
        val fileName = Paths.get(s"exercises/src/main/resources/tmp", url.split("/").lastOption.getOrElse("default.jpg"))
        val fileSink = FileIO.toPath(fileName)
        response.entity.dataBytes.runWith(fileSink)

        Behaviors.same
    }
  }
}
