package gmail.loganchazdon.dndhelper.model.database

import android.content.ContentValues
import androidx.room.OnConflictStrategy
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


//In database version 57 we moved choose, options, and chosen into a class called FeatureChoice
//we then stored an arbitrary number of FeatureChoices in each feature to allow for more
//than one choice in a single feature.
//This object is responsible for extracting already existent choices from the chosen field
//and storing them in a FeatureChoice.
val MIGRATION_56_57 = object : Migration(56, 57) {
    //Updates all the features in a passed json document.
    private fun migrateFeatureList(json: JSONArray) {
        //Migrate the JSON to version 57 formatting.
        for (featureIndex in 0 until json.length()) {
            //Use try catch in case the feature does not contain any choices.
            try {
                val jsonFeature = json.getJSONObject(featureIndex)
                val jsonChosen = jsonFeature.getJSONArray("chosen")

                //Remove the old data.
                jsonFeature.remove("options")
                jsonFeature.remove("chosen")
                jsonFeature.remove("choose")

                //Format the data to be inside a list.
                val choice = JSONObject()
                choice.put("chosen", jsonChosen)

                val choices = JSONArray()
                choices.put(choice)

                //Add the newly formatted data.
                jsonFeature.put("choices", choices)

                //Store the changes made to this feature inside the jsonFeatures object.
                json.put(featureIndex, jsonFeature)
            } catch (_: JSONException) {

            }
        }
    }

    override fun migrate(database: SupportSQLiteDatabase) {
        //Get all the character data from the db.
        val cursor = database.query("SELECT * FROM characters")
        cursor.moveToFirst()
        //Loop through each character and update them.
        for (index in 0 until cursor.count) {
            //Get the classes, background, race to be updated and the id from the old db.
            val race = cursor.getString(6)
            val background = cursor.getString(15)
            val classes = cursor.getString(11)
            val id = cursor.getInt(12)

            //Create json objects using the data from the db.
            //These objects with be updated and later inserted into the database.
            val jsonClasses = if(classes.isNullOrEmpty()) { null }  else { JSONObject(classes) }
            val jsonRace = if(race.isNullOrEmpty()) { null }  else { JSONObject(race) }
            val jsonBackground = if(background.isNullOrEmpty()) { null }  else { JSONObject(background) }


            //Get the features list from the json objects.
            val raceFeaturesJson = jsonRace?.getJSONArray("traits")
            val backgroundFeaturesJson = jsonBackground?.getJSONArray("features")

            //Update the feature lists.
            raceFeaturesJson?.let { migrateFeatureList(it) }
            backgroundFeaturesJson?.let { migrateFeatureList(it) }

            //Loop through all classes and update each one.
            jsonClasses?.keys()?.forEach {
                val levelPathJson = jsonClasses.getJSONObject(it).getJSONArray("levelPath")
                migrateFeatureList(levelPathJson)

                //Store the changes made to the levelPath in the jsonClasses Object.
                jsonClasses.getJSONObject(it).remove("levelPath")
                jsonClasses.getJSONObject(it).put("levelPath", levelPathJson)
            }

            //Store the changes made to the backgroundFeaturesJson in the backgroundJson Object.
            jsonBackground?.remove("features")
            jsonBackground?.put("features", backgroundFeaturesJson)

            //Store the changes made to the raceFeaturesJson in the jsonRace Object.
            jsonRace?.remove("traits")
            jsonRace?.put("traits", raceFeaturesJson)


            //Update the database with the new JSON.
            val values = ContentValues()
            values.put("classes", if(jsonClasses == null) {""} else {"$jsonClasses"})
            values.put("race", if(jsonRace == null) {""} else {"$jsonRace"})
            values.put("background", if(jsonBackground == null) {""} else {"$jsonBackground"})

            database.update(
                "characters", OnConflictStrategy.REPLACE, values, "id = ?",
                arrayOf(id)
            )
            cursor.moveToNext()
        }
    }
}



