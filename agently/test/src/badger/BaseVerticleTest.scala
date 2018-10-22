package badger
import java.nio.file.{Files, Path}
import java.util.UUID

import badger.tls.DistinguishedName
import io.vertx.core.Handler
import io.vertx.lang.scala.ScalaVerticle
import io.vertx.scala.core.http.{HttpClientOptions, ServerWebSocket}
import io.vertx.scala.core.net.JksOptions
import org.scalatest.Matchers
import org.scalatest.concurrent.{Eventually, ScalaFutures}

import scala.concurrent.Promise

class BaseVerticleTest extends VerticleTesting with Matchers with Eventually with ScalaFutures {

  val pathToKeystore = Files.createTempFile("verticleTest", "jks")
  println(pathToKeystore)
  val alias = s"BaseVerticleTest-${UUID.randomUUID()}"
  val (keystore, keys) =
    BaseVerticle.ensureKeystore(DistinguishedName("test"), alias, pathToKeystore)

  val hostPort = HostPort(8080)
  "BaseVerticle" should "bind to 8666 and answer with 'world'" in {
    val promise = Promise[String]

//    val cert: Path = keys.certOrGen()
//
//    val jks = keys.keyOrGen()
//    println(cert)
//    println(jks)

    val jkOps = JksOptions().setPath(pathToKeystore.toAbsolutePath.toString).setPassword(keys.keypass)
    val options = HttpClientOptions().setSsl(true).setTrustAll(true).setKeyStoreOptions(jkOps)
    vertx
      .createHttpClient(options)
      .getNow(hostPort.port, "localhost", "/rest/hello", r => {
        r.exceptionHandler(promise.failure)
        r.bodyHandler(b => promise.success(b.toString))
      })

    promise.future.futureValue shouldBe "world"
  }
  override def newVerticle(): ScalaVerticle = {
    val socketHandler = new Handler[ServerWebSocket] {
      override def handle(event: ServerWebSocket): Unit = {}
    }

    val options = BaseVerticle.serverOptions(hostPort.origin, keystore, keys.keypass)
    new BaseVerticle(socketHandler, options, hostPort, None)
  }
}
