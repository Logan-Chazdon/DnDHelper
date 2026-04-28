package gmail.loganchazdon.dndhelper.model.database.utils

import gmail.loganchazdon.database.Database
import gmail.loganchazdon.database.Features
import gmail.loganchazdon.dndhelper.model.database.gsonInstance
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
    characterId: Long = 0
): JSONArray {
    // Throw an error if we ask for character data without supplying a characterId.
    if(fillChosen) assert(characterId != 0L)

    val jsonObjects = JSONArray()
    features.forEach { feature ->
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
                            filledChoice.put("options", fillOutFeatureList(
                                features = options,
                                owner = owner,
                                fillChosen = fillChosen,
                                fillOptions= true,
                                characterId = characterId
                            ))
                        }
                    }

                    if(fillChosen) {
                        val chosen = this.characterQueries.selectFeatureChoiceChosen(
                            owner = owner,
                            choiceId = choice.id,
                            characterId = characterId
                        ).executeAsList()

                        if (chosen.isNotEmpty()) {
                            filledChoice.put("chosen", fillOutFeatureList(
                                features = chosen,
                                owner = owner,
                                fillChosen = true,
                                fillOptions= fillOptions,
                                characterId = characterId
                            ))
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

