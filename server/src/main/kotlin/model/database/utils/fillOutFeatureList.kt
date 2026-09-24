package gmail.loganchazdon.dndhelper.model.database.utils

import app.cash.sqldelight.db.QueryResult
import gmail.loganchazdon.database.Database
import gmail.loganchazdon.database.Features
import gmail.loganchazdon.dndhelper.model.database.gsonInstance
import gmail.loganchazdon.dndhelper.model.database.jsonListAdapter
import gmail.loganchazdon.dndhelper.model.database.jsonObjectAdapter
import org.json.JSONArray
import org.json.JSONObject

/** Recursively fills out all data that needs to be stored inside a feature.
 * @Param fillChosen Whether the function should fill the chosen field of the featureChoices.
 * @Param fillOptions Whether the function should fill the options field of the featureChoices.
 * @Param characterId Only used when fillChosen is true to get the correct choices for the current character.
 * */
fun Database.fillOutFeatureList(
    features: List<Features>,
    owner: String,
    fillChosen: Boolean = false,
    fillOptions: Boolean = true,
    characterId: Long = 0,
    indexes: List<Long>? = null
): JSONArray {
    // Throw an error if we ask for character data without supplying a characterId.
    if (fillChosen) assert(characterId != 0L)

    val jsonObjects = JSONArray()
    features.forEachIndexed { i, feature ->
        val json = JSONObject(gsonInstance.toJson(feature))

        json.put("spells", featureSpellCrossRefQueries.selectSpellsForFeature(owner, feature.featureId).executeAsList())
        json.put(
            "choices",
            featureChoiceEntityQueries.selectAllFor(owner, feature.featureId).executeAsList().let { choiceEntities ->
                val temp = JSONArray()
                choiceEntities.forEach { choice ->
                    val filledChoice = JSONObject(gsonInstance.toJson(choice))

                    if (fillOptions) {
                        val options = this.featuresQueries.selectFeatureOptions(
                            owner = owner,
                            featureChoiceId = choice.id
                        ).executeAsList()

                        if (options.isNotEmpty()) {
                            filledChoice.put(
                                "options", fillOutFeatureList(
                                    features = options,
                                    owner = owner,
                                    fillChosen = fillChosen,
                                    fillOptions = true,
                                    characterId = characterId
                                )
                            )
                        }
                    }

                    if (fillChosen) {
                        val chosen = this.characterQueries.selectFeatureChoiceChosen(
                            owner = owner,
                            choiceId = choice.id,
                            characterId = characterId,
                            index = indexes?.getOrNull(i)
                        ).execute { cursor ->
                            // Manual mapper to pull out index.
                            val result = mutableListOf<Pair<Long, Features>>()
                            while (cursor.next().value) {
                                result.add(
                                    (cursor.getLong(22) ?: 0) to Features(
                                        cursor.getLong(0)!!,
                                        cursor.getString(1)!!,
                                        cursor.getString(2)!!,
                                        cursor.getString(3),
                                        cursor.getLong(4)!!,
                                        cursor.getLong(5),
                                        cursor.getString(6)?.let { jsonObjectAdapter.decode(it) },
                                        jsonObjectAdapter.decode(cursor.getString(7)!!),
                                        cursor.getString(8)?.let { jsonObjectAdapter.decode(it) },
                                        cursor.getString(9),
                                        cursor.getString(10)?.let { jsonObjectAdapter.decode(it) },
                                        jsonObjectAdapter.decode(cursor.getString(11)!!),
                                        cursor.getLong(12),
                                        cursor.getLong(13),
                                        cursor.getLong(14),
                                        cursor.getString(15)?.let { jsonObjectAdapter.decode(it) },
                                        cursor.getString(16)
                                            ?.let { jsonListAdapter.decode(it) },
                                        cursor.getString(17)?.let { jsonListAdapter.decode(it) },
                                        cursor.getString(18)?.let { jsonListAdapter.decode(it) },
                                        cursor.getString(19),
                                        cursor.getLong(20),
                                        cursor.getString(21)
                                    )
                                )
                            }

                            QueryResult.Value(
                                value = result
                            )
                        }.value

                        if (chosen.isNotEmpty()) {
                            filledChoice.put(
                                "chosen", fillOutFeatureList(
                                    features = chosen.map { it.second },
                                    owner = owner,
                                    fillChosen = true,
                                    fillOptions = fillOptions,
                                    characterId = characterId,
                                    indexes = chosen.map { it.first }
                                )
                            )
                        }
                    }

                    temp.put(filledChoice)
                }
                temp
            }
        )

        jsonObjects.put(json)
    }

    return jsonObjects
}

