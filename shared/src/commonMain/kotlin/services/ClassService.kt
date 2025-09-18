package services

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.put
import model.Class
import model.ClassEntity
import model.Feature
import model.Spell
import model.pojos.NameAndIdPojo


class ClassService(client: HttpClient) : Service(client = client) {
    companion object {
        const val PATH = "class"
    }

    enum class Paths(val path: String) {
        AllClasses("$PATH/getAllClasses"),
        UnfilledClass("$PATH/getUnfilledClass"),
        InsertClass("$PATH/insertClass"),
        InsertClassFeature("$PATH/insertClassFeature"),
        InsertClassSubclass("$PATH/insertClassSubclass"),
        DeleteClassFeature("$PATH/deleteClassFeature"),
        DeleteClassSubclass("$PATH/deleteClassSubclass"),
        GetClassIdsByName("$PATH/getClassIdsByName"),
        DeleteClass("$PATH/deleteClass"),
        HomebrewClasses("$PATH/homebrewClasses"),
        ClassSpells("$PATH/classSpells"),
        GetFilledLevelPath("$PATH/getFilledLevelPath"),
        GetNamesAndIds("$PATH/getNamesAndIds"),
        GetSubclassNamesAndIds("$PATH/getSubclassNamesAndIds"),
    }

    fun getAllClasses() : Flow<List<Class>> {
        return flow {
            val response = client.get {
                url {
                    host = apiUrl
                    port = targetPort
                    path(Paths.AllClasses.path)
                }
            }.call.response.bodyAsText()
            val list = format.decodeFromString<List<ClassEntity>>(response).map {
                Class(
                    it,
                    mutableListOf()
                )
            }
            emit(list)
        }
    }

    /**Not currently configured to provide database updates. Flow is only used to allow for ui rendering.
    */
    fun getUnfilledClass(id: Int): Flow<ClassEntity> {
        return flow {
            val response = client.get {
                url {
                    host = apiUrl
                    port = targetPort
                    path("${Paths.UnfilledClass.path}/$id")
                }
            }.call.response.bodyAsText()
            emit(format.decodeFromString<ClassEntity>(response))
        }
    }

    suspend fun insertClass(classEntity: ClassEntity): Int {
        val id = client.post {
            url {
                host = apiUrl
                port = targetPort
                path(Paths.InsertClass.path)
            }
            contentType(ContentType.Application.Json)
            setBody(format.encodeToString(classEntity))
        }.bodyAsText()
        return id.toInt()
    }

    suspend fun insertClassFeatureCrossRef(featureId: Int, id: Int) {
        postTo(Paths.InsertClassFeature.path) {
            put("featureId", featureId.toString())
            put("id", id.toString())
        }
    }

    suspend fun insertClassSubclassId(classId: Int, subclassId: Int) {
        postTo(Paths.InsertClassSubclass.path) {
            put("classId", classId)
            put("subclassId", subclassId)
        }
    }

    suspend fun removeClassFeatureCrossRef(featureId: Int, id: Int) {
        deleteFrom(Paths.DeleteClassFeature.path) {
            append("featureId", featureId.toString())
            append("id", id.toString())
        }
    }

    suspend fun removeClassSubclassCrossRef(classId: Int, subclassId: Int) {
        deleteFrom(Paths.DeleteClassSubclass.path) {
            append("classId", classId.toString())
            append("subclassId", subclassId.toString())
        }
    }

    suspend fun getClassIdsByName(name: String): List<Int> {
        return format.decodeFromString(getFrom(Paths.GetClassIdsByName.path) {
            append("name", name)
        }.bodyAsText())
    }

    fun getHomebrewClasses(): Flow<List<ClassEntity>> {
        return flow {
            val response = client.get {
                url {
                    host = apiUrl
                    port = targetPort
                    path(Paths.HomebrewClasses.path)
                }
            }.call.response.bodyAsText()
            val list = format.decodeFromString<List<ClassEntity>>(response).map {
                Class(
                    it,
                    mutableListOf()
                )
            }
            emit(list)
        }
    }

    suspend fun deleteClass(id: Int) {
        deleteFrom(Paths.DeleteClass.path) {
            append("id", id.toString())
        }
    }

    suspend fun getSpellsByClassId(classId: Int): MutableList<Spell> {
        return format.decodeFromString(getFrom(Paths.ClassSpells.path) {
            append("classId", classId.toString())
        }.bodyAsText())
    }

    suspend fun getFilledLevelPath(id: Int): MutableList<Feature> {
        val result = getFrom(Paths.GetFilledLevelPath.path) {
          append("classId", id.toString())
        }.bodyAsText()
        return format.decodeFromString(result)
    }

    fun allClassesNamesAndIds(): Flow<List<NameAndIdPojo>> {
        return flow {
            val response = client.get {
                url {
                    host = apiUrl
                    port = targetPort
                    path(Paths.GetNamesAndIds.path)
                }
            }.call.response.bodyAsText()
            val list = format.decodeFromString<List<NameAndIdPojo>>(response)
            emit(list)
        }
    }

    fun getSubclassClasses(id: Int): Flow<List<NameAndIdPojo>> {
        return flow {
            val response = client.get {
                url {
                    host = apiUrl
                    port = targetPort
                    path(Paths.GetSubclassNamesAndIds.path)
                    parameters.append("classId", id.toString())
                }
            }.call.response.bodyAsText()
            val list = format.decodeFromString<List<NameAndIdPojo>>(response)
            emit(list)
        }
    }
}

