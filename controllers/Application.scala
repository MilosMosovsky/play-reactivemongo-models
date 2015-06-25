package controllers
import helpers.JsonValidation
import play.api.mvc._
import models._
import play.modules.reactivemongo.MongoController

object Application extends Controller with MongoController {
  def createFromJson = Action.async(parse.json) { request =>
    val _user = User.createFromJson(request.body)
    JsonValidation.toRequest(_user)
  }


}