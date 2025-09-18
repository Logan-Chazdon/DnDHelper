package gmail.loganchazdon.dndhelper.model.database.utils

import gmail.loganchazdon.database.Database
import gmail.loganchazdon.database.Features
import gmail.loganchazdon.dndhelper.model.database.gsonInstance
import org.json.JSONArray
import org.json.JSONObject

fun Database.fillOutFeatureListWithoutChosen(
    features: List<Features>,
    owner: String
): JSONArray {
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
                    val options = this.featuresQueries.selectFeatureOptions(
                        owner = owner,
                        featureChoiceId = choice.id
                    ).executeAsList()

                    if(options.isNotEmpty()) {
                        filledChoice.put("options", fillOutFeatureListWithoutChosen(options, owner))
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

