package lib

import controllers.Application._
import play.api.libs.json._
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.bson.BSONObjectID
import scala.concurrent.{ExecutionContext}
import ExecutionContext.Implicits.global

import scala.concurrent.{Promise, Future}

abstract class MongoTable[T](table: String) {

  def collection: JSONCollection = db.collection[JSONCollection](table)

  val generateMongoId = (__ \ '_id \ '$oid).json.put(JsString(BSONObjectID.generate.stringify))
  val addMongoId: Reads[JsObject] = __.json.update(generateMongoId)

  def getMongoId(_obj: JsObject): String = {
    (_obj \ "_id" \ "$oid").as[String]
  }

  def appendMongoId(json: JsValue): JsResult[JsObject] = {
    json.transform(addMongoId)
  }

  def createFromJson(json: JsValue): Future[T] = {
    val p = Promise[T]()
    val clazz = Class.forName(super.getClass.getName)
    val reflection = clazz.getField("MODULE$").get(classOf[MongoModel]).asInstanceOf[MongoModel]
    reflection.validator(json).map { _validatedClazz =>
      this.appendMongoId(json).map { _insertionObject =>
        collection.insert(_insertionObject).map { lastError =>
          p.success(_insertionObject.asInstanceOf[T])
        }
      }
    } recoverTotal { _e =>
      p.failure(new Exception)
    }
    p.future
  }
}