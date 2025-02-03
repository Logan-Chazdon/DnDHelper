package gmail.loganchazdon.dndhelper.model.services

import gmail.loganchazdon.database.Classes
import gmail.loganchazdon.database.Database
import gmail.loganchazdon.dndhelper.model.database.*
import io.ktor.client.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.json.JSONObject

fun Routing.classService(db: Database, httpClient: HttpClient) {
    get("class/getAllClasses") {
        val session = getSession(call)
        val userInfo = session?.let { getUserInfo(httpClient, it, call) }
        val query = db.classesQueries.getAllClassesFor(userInfo?.id).executeAsList()

        call.respondText(gson.toJson(query).clean())
    }

    get("class/homebrewClasses") {
        withUserInfo {
            call.respondText(
                gson.toJson(
                    db.classesQueries.getHomebrewClasses(it.id).executeAsList()
                ).clean()
            )
        }
    }
    get("class/getUnfilledClass/{id}") {
        withUserInfo { userInfo: UserInfo ->
            try {
                val id = call.parameters["id"]!!.toLong()
                val query = db.classesQueries.getClassById(id = id, owner = userInfo.id).executeAsOne()
                call.respondText(
                    gson.toJson(
                        query
                    ).clean()
                )
            } catch (e: Exception) {
                call.respond(HttpStatusCode(400, "Invalid Id"))
            }
        }
    }


    get("class/getClassIdsByName/{name}") {
        withUserInfo { userInfo ->
            call.respondText(
                gson.toJson(
                    db.classesQueries.getClassIdsByName(
                        call.parameters["name"]!!,
                        userInfo.id
                    ).executeAsList()
                )
            )
        }
    }

    get("class/classSpells/{classId}") {
        withUserInfo { userInfo ->
            call.respondText(
                gson.toJson(
                    db.classSpellCrossRefQueries.selectSpellsByClass(
                        call.parameters["classId"]!!.toLong(),
                        userInfo.id
                    ).executeAsList()
                )
            )
        }
    }

    get("class/getUnfilledLevelPath") {
        withUserInfo { userInfo ->
            val data = db.classesQueries.selectLevelPath(
                call.parameters["classId"]!!.toLong(),
                userInfo.id
            ).executeAsList()
            val response = gson.toJson(
                data
            )
            call.respondText(response)
        }
    }

    get("class/getNamesAndIds") {
        withUserInfo { userInfo ->
            call.respondText(
                gson.toJson(
                    db.classesQueries.selectIdNameFor(
                        userInfo.id
                    ).executeAsList()
                )
            )
        }
    }

    get("class/getSubclassNamesAndIds") {
        withUserInfo { userInfo ->
            call.respondText(
                gson.toJson(
                    db.subclassesQueries.selectIdNameFor(
                        userInfo.id,
                        call.parameters["classId"]!!.toLong()
                    ).executeAsList()
                )
            )
        }
    }




    post("class/insertClass") {
        withUserInfo { userInfo ->
            val response = call.receiveText()
            val clazz = gson.fromJson(response, Classes::class.java)
            db.classesQueries.insertClass(clazz.copy(owner = userInfo.id))
            call.respondText(clazz.id.toString(), status = HttpStatusCode.OK)
        }
    }

    post("class/insertClassFeature") {
        withUserInfo { userInfo ->
            val body = JSONObject(call.receiveText())

            db.classFeatureCrossRefQueries.insert(
                featureId = body.getLong("featureId"),
                id = body.getLong("id"),
                owner = userInfo.id
            )
            call.respond(status = HttpStatusCode.OK, "Inserted")
        }
    }

    post("class/insertClassSubclass") {
        withUserInfo { userInfo ->
            val body = JSONObject(call.receiveText())

            db.classSubclassCrossRefQueries.insert(
                subclassId = body.getLong("subclassId"),
                classId = body.getLong("classId"),
                owner = userInfo.id
            )
            call.respond(status = HttpStatusCode.OK, "Inserted")
        }
    }




    delete("class/deleteClassFeature") {
        withUserInfo { userInfo ->
            db.classFeatureCrossRefQueries.delete(
                owner = userInfo.id,
                classId = call.parameters["id"]!!.toLong(),
                featureId = call.parameters["featureId"]!!.toLong()
            )
            call.respond(HttpStatusCode.OK, "Item deleted")
        }
    }

    delete("class/deleteClassSubclass") {
        withUserInfo { userInfo ->
            db.classSubclassCrossRefQueries.deleteBy(
                owner = userInfo.id,
                classId = call.parameters["classId"]!!.toLong(),
                subclassId = call.parameters["subclassId"]!!.toLong()
            )
            call.respond(HttpStatusCode.OK, "Item deleted")
        }
    }

    delete("class/deleteClass") {
        withUserInfo { userInfo ->
            db.classesQueries.deleteClass(
                owner = userInfo.id,
                id = call.parameters["id"]!!.toLong(),
            )
            call.respond(HttpStatusCode.OK, "Item deleted")
        }
    }
}
