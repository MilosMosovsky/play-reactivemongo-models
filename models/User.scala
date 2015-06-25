package models

import lib.{MongoModel, MongoTable}

import play.api.libs.json._
case class User( browser: String )

object User extends MongoTable[User]("user") with MongoModel {
  implicit val userFormat = Json.format[User]
  def validator(json: JsValue) = json.validate[User]
}


