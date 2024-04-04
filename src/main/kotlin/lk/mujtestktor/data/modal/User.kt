package lk.mujtestktor.data.modal

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class User(
    val email: String,
    val username: String,
    val password: String,
    @BsonId
    val id : String = ObjectId().toString() //This is to allow MongoDb to know this is a primary key & Auto generated
)
