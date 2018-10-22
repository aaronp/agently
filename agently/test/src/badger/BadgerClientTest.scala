package badger
import org.scalatest.{Matchers, WordSpec}
import scalaj.http.{Http, HttpOptions, HttpResponse}

import scala.concurrent.Await
import scala.concurrent.duration._


class BadgerClientTest extends WordSpec with Matchers {

  "BadgerClient" should {
    "be abe to connect" in {

      val hostPort = HostPort(7778).withOrigin(BaseVerticle.host.hostname())
      val started = BaseVerticle.startOn(hostPort)
      try {

        val restHelloUrl = s"https://${BaseVerticle.host.hostname()}:${hostPort.port}/rest/hello"
          println(restHelloUrl)
        val world: HttpResponse[String] = Http(restHelloUrl).option(HttpOptions.allowUnsafeSSL).asString
        world.body shouldBe "world"
      } finally {
        val fut = started.vertxInstance.closeFuture()
        Await.result(fut, testTimeout)
      }
    }
  }

  def testTimeout = 5.seconds
}
