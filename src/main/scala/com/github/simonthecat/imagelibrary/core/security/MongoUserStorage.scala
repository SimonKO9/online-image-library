package com.github.simonthecat.imagelibrary.core.security

import org.apache.logging.log4j.LogManager
import reactivemongo.api.DB
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson.{Macros, BSONDocument}

import scala.concurrent.{ExecutionContext, Future}

class MongoUserStorage(implicit val db: DB, implicit val ec: ExecutionContext) extends UserStorage {

  val collection: BSONCollection = db.collection("users")
  
  implicit val handler = Macros.handler[StoredUser]

  val log = LogManager.getLogger(this.getClass)
  
  override def getUser(username: String): Future[Option[StoredUser]] = {
    log.debug(s"Retrieving user from mongo for username=$username")
    collection.find(BSONDocument(("username", username))).cursor[StoredUser].headOption
  }
}
