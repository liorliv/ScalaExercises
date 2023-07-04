import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.scaladsl.Behaviors
import akka.util.Timeout
import akkascrapper.ImageDownloadActor

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.Future
import scala.util.{Failure, Success}

object Main extends App {
  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "HtmlImageDownloader")
  implicit val timeout: Timeout = Timeout(5.minutes)

  val htmlParsingActor = system.systemActorOf(HtmlParsingActor(), "HtmlParsingActor")
  val imageDownloadActor = system.systemActorOf(ImageDownloadActor(), "ImageDownloadActor")
  val fileSystemActor = system.systemActorOf(FileSystemActor(), "FileSystemActor")

  val htmlPageUrl = "https://salt.security/"

  val imageUrlsFuture = htmlParsingActor ? (HtmlParsingActor.ParseHtml(htmlPageUrl, _))
  val imageUrlsAndResponsesFuture = imageUrlsFuture.map { imageUrls =>
    imageUrls.map { imageUrl =>
      val responseFuture = imageDownloadActor ? (ImageDownloadActor.DownloadImage(imageUrl, _))
      responseFuture.map(response => response)
    }
  }

  val imageSavingFuture = imageUrlsAndResponsesFuture.flatMap { imageUrlsAndResponses =>
    Future.sequence(imageUrlsAndResponses)
  }.map { imageUrlsAndResponses =>
    imageUrlsAndResponses.foreach { response =>
        fileSystemActor ! FileSystemActor.SaveImage(response.url, response.res)
    }
    "All images saved successfully!"
  }

  imageSavingFuture.onComplete {
    case Success(result) =>
      println(result)
      system.terminate()
    case Failure(ex) =>
      println(s"Failed to save images: ${ex.getMessage}")
      system.terminate()
  }

}
