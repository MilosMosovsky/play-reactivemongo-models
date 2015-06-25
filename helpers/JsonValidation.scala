package helpers

import play.api.Logger
import play.api.libs.json.{JsObject, JsString}
import play.api.mvc.Results._
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future, Promise}

object JsonValidation {


  val err = JsObject(Seq(
    "error" -> JsString("Invalid Json"))
  )
  def toRequest(json : Future[Any]) : Future[SimpleResult] = {
    val p = Promise[SimpleResult]()
    json.map { _result =>
      Logger.debug(_result.getClass.toString)
      p.success(Created(_result.toString).as("application/json"))
    } onFailure {
      case t : Exception =>
        Logger.debug(t.toString)
        p.success(BadRequest(err.toString).as("application/json"))
      case _ => p.success(InternalServerError)
    }

    p.future
  }

}