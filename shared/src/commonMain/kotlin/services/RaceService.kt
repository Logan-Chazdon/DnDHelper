package services

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.put
import model.FeatChoiceEntity
import model.Feature
import model.Race
import model.RaceEntity
import model.pojos.NameAndIdPojo

class RaceService (client: HttpClient) : Service(client = client) {
    companion object {
        const val PATH = "race"
    }

    enum class Paths(val path: String) {
        InsertRace("$PATH/insertRace"),
        InsertRaceFeature("$PATH/insertRaceFeature"),
        DeleteRaceFeature("$PATH/deleteRaceFeature"),
        InsertRaceSubrace("$PATH/insertRaceSubrace"),
        RaceFeatures("$PATH/raceFeatures"),
        SubraceFeatures("$PATH/subraceFeatures"),
        SubraceFeatChoices("$PATH/subraceFeatChoices"),
        AllRaces("$PATH/allRaces"),
        DeleteRace("$PATH/deleteRace"),
        HomebrewRaces("$PATH/homebrewRaces"),
        GetLiveRace("$PATH/getLiveRace"),
        GetAllRaceNameIds("$PATH/getAllRaceNameIds"),
    }

    suspend fun insertRace(newRace: RaceEntity): Int {
        val id = client.post {
            url {
                host = apiUrl
                port = targetPort
                path(Paths.InsertRace.path)
            }
            contentType(ContentType.Application.Json)
            setBody(format.encodeToString(newRace))
        }.bodyAsText()
        return id.toInt()
    }

    suspend fun insertRaceFeatureCrossRef(featureId: Int, raceId: Int) {
        postTo(Paths.InsertRaceFeature.path) {
            put("featureId", featureId)
            put("raceId", raceId)
        }
    }

    suspend fun removeRaceFeatureCrossRef(featureId: Int, raceId: Int) {
        deleteFrom(Paths.DeleteRaceFeature.path) {
            append("featureId", featureId.toString())
            append("raceId", raceId.toString())
        }
    }

    suspend fun insertRaceSubraceCrossRef(subraceId: Int, raceId: Int) {
        postTo(Paths.InsertRaceSubrace.path) {
            put("subraceId", subraceId)
            put("raceId", raceId)
        }
    }

    suspend fun getRaceFeatures(raceId: Int): List<Feature> {
        return format.decodeFromString(getFrom(Paths.RaceFeatures.path) {
            append("raceId", raceId.toString())
        }.bodyAsText())
    }

    suspend fun getSubraceFeatures(subraceId: Int): List<Feature> {
        return format.decodeFromString(getFrom(Paths.SubraceFeatures.path) {
            append("subraceId", subraceId.toString())
        }.bodyAsText())
    }

    suspend fun getSubraceFeatChoices(id: Int): List<FeatChoiceEntity> {
        return format.decodeFromString(getFrom(Paths.SubraceFeatChoices.path) {
            append("id", id.toString())
        }.bodyAsText())
    }

    fun getAllRaces(): Flow<List<Race>> {
        return flow {
            val response = client.get {
                url {
                    host = apiUrl
                    port = targetPort
                    path(Paths.AllRaces.path)
                }
            }.call.response.bodyAsText()
            val list = format.decodeFromString<List<Race>>(response)
            emit(list)
        }
    }

    suspend fun deleteRace(id: Int) {
        deleteFrom(Paths.DeleteRace.path) {
            append("id", id.toString())
        }
    }

    fun getHomebrewRaces(): Flow<List<Race>> {
        return flow {
            val response = client.get {
                url {
                    host = apiUrl
                    port = targetPort
                    path(Paths.HomebrewRaces.path)
                }
            }.call.response.bodyAsText()
            val list = format.decodeFromString<List<Race>>(response)
            emit(list)
        }
    }

    fun findUnfilledLiveRaceById(id: Int): Flow<Race> {
        return flow {
            client.webSocket(
                method = HttpMethod.Get,
                host = apiUrl,
                port = targetPort,
                path = Paths.GetLiveRace.path
            ) {
                send(Frame.Text(id.toString()))
                while (true) {
                    val othersMessage = incoming.receive() as? Frame.Text
                    val listToEmit = format.decodeFromString<Race>(othersMessage!!.readText())
                    emit(listToEmit)
                }
            }
        }
    }


    fun getRaceSubraces(id: Int): Flow<List<NameAndIdPojo>> {
        return flow {
            client.webSocket(
                method = HttpMethod.Get,
                host = apiUrl,
                port = targetPort,
                path = Paths.GetLiveRace.path
            ) {
                send(Frame.Text(id.toString()))
                while (true) {
                    val othersMessage = incoming.receive() as? Frame.Text
                    val listToEmit = Json.decodeFromString<List<NameAndIdPojo>>(othersMessage!!.readText())
                    emit(listToEmit)
                }
            }
        }
    }

    fun getAllRaceIdsAndNames(): Flow<List<NameAndIdPojo>> {
        return flow {
            client.webSocket(
                method = HttpMethod.Get,
                host = apiUrl,
                port = targetPort,
                path = Paths.GetAllRaceNameIds.path
            ) {
                send("")
                while (true) {
                    val othersMessage = incoming.receive() as? Frame.Text
                    val listToEmit = Json.decodeFromString<List<NameAndIdPojo>>(othersMessage!!.readText())
                    emit(listToEmit)
                }
            }
        }
    }


}