package kr.scala.experiments.tests.spray

import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import kr.scala.experiments.tests.AbstractExperimentTest
import org.scalatest.Ignore
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Await, Future}
import spray.can.Http
import spray.client.pipelining._
import spray.http._
import spray.httpx.encoding.Gzip

/**
 * SprayClientTest
 * @author Sunghyouk Bae
 */
@Ignore
class SprayClientTest extends AbstractExperimentTest {

    implicit val system = akka.actor.ActorSystem("spray")
    implicit val executor = ExecutionContext.fromExecutor(scala.concurrent.ExecutionContext.Implicits.global)
    implicit val timeout = Timeout(10 seconds)

    test("simple async get") {
        val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
        val response = pipeline(Get("http://spray.io"))

        val result = Await.result(response, 15 seconds)
        println(result.entity.asString(HttpCharsets.`UTF-8`))
    }

    test("connector setup") {
        val pipeline: Future[SendReceive] =
            for (
                Http.HostConnectorInfo(connector, _) <- IO(Http) ? Http.HostConnectorSetup("spray.io", port = 80)
            ) yield sendReceive(connector)

        val request = Get("/")
        val response: Future[HttpResponse] = pipeline.flatMap(_(request))
        val result: HttpResponse = Await.result(response, 15 seconds)

        println(Gzip.decode(result.message).toString)
        // println(Gzip.decode(result.message).entity.toString) //.asString(HttpCharsets.`UTF-8`))
    }
}
