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
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.put
import model.*


class FeatureService(client: HttpClient) : Service(client = client) {
    enum class Paths(val path: String) {
        InsertFeature("$PATH/insertFeature"),
        InsertFeatureOptions("$PATH/insertFeatureOptions"),
        InsertFeatureChoice("$PATH/insertFeatureChoice"),
        DeleteFeatureOptions("$PATH/deleteFeatureOptions"),
        DeleteOptionsFeature("$PATH/deleteOptionsFeature"),
        InsertFeatureChoiceIndex("$PATH/insertFeatureChoiceIndex"),
        InsertIndex("$PATH/insertIndex"),
        RemoveIdFromRef("$PATH/removeIdFromRef"),
        InsertFeatureSpell("$PATH/insertFeatureSpell"),
        RemoveFeatureSpell("$PATH/removeFeatureSpell"),
        InsertOptionsFeature("$PATH/insertOptionsFeature"),
        DeleteFeatureFeatureChoice("$PATH/deleteFeatureFeatureChoice"),
        FeatureChoices("$PATH/featureChoices"),
        FeatureSpells("$PATH/featureSpells"),
        FeatureChoiceOptions("$PATH/featureChoiceOptions"),
        ClearFeatureChoiceIndexRefs("$PATH/clearFeatureChoiceIndexRefs"),
        GetFeatureIdFromSpell("$PATH/getFeatureIdFromSpell"),
        LiveFeature("$PATH/liveFeature"),
        LiveFeatureChoices("$PATH/liveFeatureChoices"),
        LiveFeatureSpells("$PATH/liveFeatureSpells"),
        LiveAllIndexes("$PATH/liveAllIndexes"),
    }

    companion object {
        const val PATH = "feature"
    }

    suspend fun fillOutFeatureListWithoutChosen(features: List<Feature>) {
        features.forEach { feature ->
            feature.spells = getFeatureSpells(feature.featureId)
            feature.choices = getFeatureChoices(feature.featureId).let { choiceEntities ->
                val temp = mutableListOf<FeatureChoice>()
                choiceEntities.forEach { choice ->
                    val filledChoice = FeatureChoice(
                        entity = choice,
                        options = getFeatureChoiceOptions(choice.id),
                        chosen = null
                    )
                    filledChoice.options?.let { fillOutFeatureListWithoutChosen(it) }
                    temp.add(
                        filledChoice
                    )
                }
                temp
            }
        }
    }

    suspend fun insertFeature(feature: FeatureEntity): Int {
        val id = client.post {
            url {
                host = apiUrl
                port = targetPort
                path(Paths.InsertFeature.path)
            }
            contentType(ContentType.Application.Json)
            setBody(format.encodeToString(feature))
        }.bodyAsText()
        return id.toInt()
    }

    suspend fun insertFeatureOptionsCrossRef(featureId: Int, id: Int) {
        postTo(Paths.InsertFeatureOptions.path) {
            put("featureId", featureId)
            put("id", id)
        }
    }

    suspend fun insertFeatureChoice(option: FeatureChoiceEntity): Int {
        val id = client.post {
            url {
                host = apiUrl
                port = targetPort
                path(Paths.InsertFeatureChoice.path)
            }
            contentType(ContentType.Application.Json)
            setBody(format.encodeToString(option))
        }.bodyAsText()
        return id.toInt()
    }

    suspend fun removeFeatureOptionsCrossRef(featureId: Int, id: Int) {
        deleteFrom(Paths.DeleteFeatureOptions.path) {
            append("featureId", featureId.toString())
            append("id", id.toString())
        }
    }

    suspend fun removeOptionsFeatureCrossRef(featureId: Int, choiceId: Int) {
        deleteFrom(Paths.DeleteOptionsFeature.path) {
            append("featureId", featureId.toString())
            append("choiceId", choiceId.toString())
        }
    }

    suspend fun insertFeatureChoiceIndexCrossRef(
        choiceId: Int,
        index: String,
        levels: List<Int>?,
        classes: List<String>?,
        schools: List<String>?
    ) {
        postTo(Paths.InsertFeatureChoiceIndex.path) {
            put("choiceId", choiceId)
            put("index", index)
            put("levels", format.encodeToJsonElement(levels))
            put("classes", format.encodeToJsonElement(classes))
            put("schools", format.encodeToJsonElement(schools))
        }
    }

    suspend fun insertIndexRef(index: String, ids: List<Int>) {
        postTo(Paths.InsertIndex.path) {
            put("index", index)
            put("ids", format.encodeToJsonElement(ids))
        }
    }

    suspend fun removeIdFromRef(id: Int, ref: String) {
        postTo(Paths.RemoveIdFromRef.path) {
            put("id", id)
            put("ref", ref)
        }
    }

    suspend fun insertFeatureSpellCrossRef(spellId: Int, featureId: Int) {
        postTo(Paths.InsertFeatureSpell.path) {
            put("spellId", spellId)
            put("featureId", featureId)
        }
    }

    suspend fun removeFeatureSpellCrossRef(spellId: Int, featureId: Int) {
        deleteFrom(Paths.RemoveFeatureSpell.path) {
            append("spellId", spellId.toString())
            append("featureId", featureId.toString())
        }
    }

    suspend fun insertOptionsFeatureCrossRef(featureId: Int, choiceId: Int) {
        postTo(Paths.InsertOptionsFeature.path) {
            put("featureId", featureId)
            put("choiceId", choiceId)
        }
    }

    suspend fun removeFeatureFeatureChoice(choiceId: Int, characterId: Int) {
        deleteFrom(Paths.DeleteFeatureFeatureChoice.path) {
            append("choiceId", choiceId.toString())
            append("characterId", characterId.toString())
        }
    }

    suspend fun getFeatureChoices(featureId: Int): List<FeatureChoiceEntity> {
        return format.decodeFromString(getFrom(Paths.FeatureChoices.path) {
            append("featureId", featureId.toString())
        }.bodyAsText())
    }

    suspend fun getFeatureSpells(featureId: Int): List<Spell>? {
        return format.decodeFromString(getFrom(Paths.FeatureSpells.path) {
            append("featureId", featureId.toString())
        }.bodyAsText())
    }

    fun getLiveFeatureById(id: Int): Flow<Feature> {
        return flow {
            client.webSocket(method = HttpMethod.Get, host = apiUrl, port = targetPort, path = Paths.LiveFeature.path) {
                while (true) {
                    send(Frame.Text(id.toString()))
                    val othersMessage = incoming.receive() as? Frame.Text
                    println(othersMessage?.readText())
                    if (othersMessage?.readText() != "Invalid Id") {
                        val feature = format.decodeFromString<Feature>(
                            othersMessage!!.readText()
                        )
                        emit(feature)
                    }
                }
            }
        }
    }

    fun getLiveFeatureChoices(featureId: Int): Flow<List<FeatureChoiceEntity>> {
        return flow {
            client.webSocket(method = HttpMethod.Get, host = apiUrl, port = targetPort, path = Paths.LiveFeatureChoices.path) {
                while (true) {
                    send(Frame.Text(featureId.toString()))
                    val othersMessage = incoming.receive() as? Frame.Text
                    println(othersMessage?.readText())
                    if (othersMessage?.readText() != "Invalid Id") {
                        val feature = format.decodeFromString<List<FeatureChoiceEntity>>(
                            othersMessage!!.readText()
                        )
                        emit(feature)
                    }
                }
            }
        }
    }

    suspend fun getFeatureChoiceOptions(featureChoiceId: Int): List<Feature> {
        return format.decodeFromString(getFrom(Paths.FeatureChoiceOptions.path) {
            append("featureChoiceId", featureChoiceId.toString())
        }.bodyAsText())
    }

    suspend fun clearFeatureChoiceIndexRefs(id: Int) {
        deleteFrom(Paths.ClearFeatureChoiceIndexRefs.path) {
            append("id", id.toString())
        }
    }

    suspend fun getFeatureIdOr0FromSpellId(id: Int): Int {
        return getFrom(Paths.GetFeatureIdFromSpell.path) {
            append("id", id.toString())
        }.bodyAsText().toInt()
    }

    fun getLiveFeatureSpells(id: Int): Flow<List<Spell>?> {
        return flow {
            client.webSocket(method = HttpMethod.Get, host = apiUrl, port = targetPort, path = Paths.LiveFeatureSpells.path) {
                while (true) {
                    send(Frame.Text(id.toString()))
                    val othersMessage = incoming.receive() as? Frame.Text
                    println(othersMessage?.readText())
                    if (othersMessage?.readText() != "Invalid Id") {
                        val list = format.decodeFromString<List<Spell>?>(
                            othersMessage!!.readText()
                        )
                        emit(list)
                    }
                }
            }
        }
    }

    fun returnGetAllIndexes(): Flow<List<String>> {
        return flow {
            client.webSocket(method = HttpMethod.Get, host = apiUrl, port = targetPort, path = Paths.LiveAllIndexes.path) {
                while (true) {
                    val othersMessage = incoming.receive() as? Frame.Text
                    println(othersMessage?.readText())
                    if (othersMessage?.readText() != "Invalid Id") {
                        val list = format.decodeFromString<List<String>>(
                            othersMessage!!.readText()
                        )
                        emit(list)
                    }
                }
            }
        }
    }
}