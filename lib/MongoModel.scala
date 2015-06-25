package lib

import _root_.play.api.libs.json.JsResult
import _root_.play.api.libs.json.JsValue
import play.api.libs.json.{JsResult, JsValue}

trait MongoModel {
  def validator(json: JsValue): JsResult[Any]
}