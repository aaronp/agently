package badger
import io.vertx.lang.scala.{ScalaVerticle, VertxExecutionContext}
import io.vertx.scala.core.Vertx
import org.scalatest.{AsyncFlatSpec, BeforeAndAfter}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success, Try}

abstract class VerticleTesting extends AsyncFlatSpec with BeforeAndAfter {
  protected val vertx: Vertx = Vertx.vertx
  implicit protected val vertxExecutionContext: VertxExecutionContext = VertxExecutionContext(vertx.getOrCreateContext)

  private var deploymentId: String = ""
  private val duration: Duration = 5.seconds

  def newVerticle() : ScalaVerticle

  before {
    val verticle = newVerticle()
    deploymentId = Await.result(
      vertx
        .deployVerticleFuture(verticle)
        .andThen {
          case Success(id) => id   // typical value: 2115d175-e724-4f2a-aaa6-2dadbf733370
          case Failure(throwable) => throw throwable
        },
      duration
    )
  }

  after {
    Try {
      Await.ready(
        vertx
          .undeployFuture(deploymentId)
          .andThen {
            case Success(unit) => unit
            case Failure(throwable) => throw throwable
          },
        duration
      )
    }
  }
}