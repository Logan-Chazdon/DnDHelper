package com.example.dndhelper.repository.webServices

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dndhelper.AppModule
import com.example.dndhelper.repository.dataClasses.*
import dagger.Component
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import java.lang.IllegalStateException


@Component(modules = [AppModule::class])
interface Webservice {

}

class WebserviceDnD() : Webservice {
    private val baseUrl = "https://www.dnd5eapi.co"

    fun getClasses(_classes : MutableLiveData<List<Class>>): LiveData<List<Class>> {
        GlobalScope.launch {
            val classes : MutableList<Class> = mutableListOf()
            var result =
                URL("$baseUrl/api/classes").readText()
            var jsonContact =
                JSONObject(result)
            val jsonArrayOfURls = jsonContact.getJSONArray("results")
            val classURLS : MutableList<String> = mutableListOf()
            for(i in 0 until jsonArrayOfURls.length())
            {
                val jsonObject = jsonArrayOfURls.getJSONObject(i)
                classURLS.add(i, jsonObject.getString("url"))
                classes.add(Class(jsonObject.getString("name")))

            }

            //Through every class
            for ((x, Class) in classURLS.withIndex()) {
                val classUrl = classURLS[x]
                //Through every level
                for (j in 1..20) {
                    val apiCall =
                        "https://www.dnd5eapi.co$classUrl/levels/$j"

                    result = URL(apiCall).readText()
                    jsonContact = JSONObject(result)
                    val jsonarrayInfo: JSONArray = jsonContact.getJSONArray("features")

                    val size: Int = jsonarrayInfo.length()
                    //Through every feature in this level
                    for (i in 0 until size) {
                        var numOfChoices: Int = 0
                        var features = mutableListOf<Feature>()
                        val json_objectdetail: JSONObject = jsonarrayInfo.getJSONObject(i)
                        val featureName = json_objectdetail.getString("name")
                        val url = "https://www.dnd5eapi.co" + json_objectdetail.get("url")
                        val featureResult = URL(url).readText()
                        val featureDesc = JSONObject(featureResult).getString("desc")
                        //See if the feature has choices
                        try {
                            //Get a list of the choices from the api
                            val featureJson: JSONObject =
                                JSONObject(featureResult).get("feature_specific") as JSONObject
                            val subFeatureJson: JSONObject =
                                featureJson.get("subfeature_options") as JSONObject

                            //Get data about the choice
                            numOfChoices = subFeatureJson.getInt("choose")
                            val choicesJson = subFeatureJson.getJSONArray("from")
                            features = mutableListOf<Feature>()

                            for (y in 0 until choicesJson.length()) {
                                //Get the data about the feature from the api
                                val feature: JSONObject = choicesJson.getJSONObject(y)
                                val featureDetailsUrl =
                                    "https://www.dnd5eapi.co" + feature.get("url")
                                val featureDetails: JSONObject =
                                    JSONObject(URL(featureDetailsUrl).readText())

                                //Create a featureObject with the data
                                val name = featureDetails.getString("name")
                                val description = featureDetails.getString("desc")
                                features.add(i, Feature(name, description, j, 0,null))
                            }
                        } catch (e: Exception) {

                        }
                        val feature = Feature(
                            name = featureName,
                            description = featureDesc,
                            choiceNum = numOfChoices,
                            level = j,
                            options = features
                        )
                        classes[x].levelPath.add(feature)
                    }
                }
            }
            _classes.postValue(classes)
        }
        return _classes
    }

    fun getRaces(_races : MutableLiveData<List<Race>>) {
        GlobalScope.launch {
            val races = mutableListOf<Race>()
            var result =
                URL("$baseUrl/api/races").readText()
            var raceUrlsJsonObject =
                JSONObject(result)
            val jsonArrayOfURls = raceUrlsJsonObject.getJSONArray("results")
            val raceURLS : MutableList<String> = mutableListOf()

            for(i in 0 until jsonArrayOfURls.length())
            {
                val jsonObject = jsonArrayOfURls.getJSONObject(i)
                raceURLS.add(i, jsonObject.getString("url"))
            }

            for((i, url) in raceURLS.withIndex()) {
                val raceJsonObject = JSONObject(URL("$baseUrl$url").readText())
                val abilityBonuses = extractAbilityBonuses(raceJsonObject)
                val startingProficiencies = extractStartingProficiencies(raceJsonObject)
                val languages = extractLanguages(raceJsonObject)
                val traits = extractTraits(raceJsonObject)
                val subraces = mutableListOf<Subrace>()
                val size = when(raceJsonObject.getString("size")) {
                    "Tiny" -> SizeClass.Tiny()
                    "Small" -> SizeClass.Small()
                    "Medium" -> SizeClass.Medium()
                    "Large" -> SizeClass.Large()
                    "Huge" -> SizeClass.Huge()
                    "Gargantuan" -> SizeClass.Gargantuan()
                    else -> {
                        throw IllegalStateException("Illegal Size Class API Data")
                    }
                }


                val subRacesJsonObject = raceJsonObject.getJSONArray("subraces")
                for(j in 0 until subRacesJsonObject.length()) {
                    val subRaceJson = subRacesJsonObject.getJSONObject(j)
                    val subUrl = subRaceJson.getString("url")
                    val subRaceDetailsJson = JSONObject(URL("$baseUrl$subUrl").readText())

                    val subRaceAbilityBonuses= extractAbilityBonuses(subRaceDetailsJson)
                    val subRaceStartingProficiencies = extractStartingProficiencies(subRaceDetailsJson)
                    val subRaceLanguages = extractLanguages(subRaceDetailsJson)
                    val racialTraits = extractTraits(subRaceDetailsJson)
                    val subRaceLanguageOptions = mutableListOf<Language>()

                    try {
                        val subRaceLanguageOptionsJson = subRaceDetailsJson.getJSONArray("language_options")
                        for(x in 0 until subRacesJsonObject.length()) {
                            val subRaceLanguageJson = subRaceLanguageOptionsJson.getJSONObject(x)
                            subRaceLanguageOptions[x] = Language(name = subRaceLanguageJson.getString("name"))
                        }
                    } catch(e: Exception) { }

                    subraces.add(j, Subrace(
                        name = subRaceDetailsJson.getString("name"),
                        desc = subRaceDetailsJson.getString("desc"),
                        abilityBonuses = subRaceAbilityBonuses,
                        startingProficiencies = subRaceStartingProficiencies,
                        languages = subRaceLanguages,
                        languageOptions = subRaceLanguageOptions ,
                        numOfLanguageChoices = try {subRaceDetailsJson
                            .getJSONObject("language_options")
                            .getInt("choose") } catch (e : java.lang.Exception) { 0 },
                        racialTraits = racialTraits))
                }


                races.add(i, Race(
                 name = raceJsonObject.getString("name"),
                 groundSpeed = raceJsonObject.getInt("speed"),
                 abilityBonuses = abilityBonuses,
                 alignment = raceJsonObject.getString("alignment"),
                 age = raceJsonObject.getString("age"),
                 size = size,
                 sizeDesc = raceJsonObject.getString("size_description"),
                 startingProficiencies = startingProficiencies,
                 languages = languages,
                 languageDesc = raceJsonObject.getString("language_desc"),
                 traits = traits,
                 subraces = subraces
                ))
            }
            _races.postValue(races)
        }
    }

    private fun extractAbilityBonuses(jsonObject: JSONObject) : List<AbilityBonus> {
        val abilityBonuses = mutableListOf<AbilityBonus>()
        val abilityBonusesJsonObject = jsonObject.getJSONArray("ability_bonuses")
        for(j in 0 until abilityBonusesJsonObject.length()) {
            val abilityBonusJson = abilityBonusesJsonObject.getJSONObject(j)
            val ability: Ability = when(abilityBonusJson
                .getJSONObject("ability_score")
                .getString("name")
            ) {
                "STR" -> Ability.Strength()
                "DEX" -> Ability.Dexterity()
                "CON" -> Ability.Constitution()
                "INT" -> Ability.Intelligence()
                "WIS" -> Ability.Wisdom()
                "CHA" -> Ability.Charisma()
                else -> {
                    throw IllegalStateException("Illegal AbilityScore API Data")
                }
            }
            abilityBonuses.add(j, AbilityBonus(
                ability = ability,
                bonus = abilityBonusJson.getInt("bonus")
            ))
        }
        return abilityBonuses
    }

    private fun extractStartingProficiencies(jsonObject: JSONObject) : List<Proficiency> {
        val startingProficiencies = mutableListOf<Proficiency>()
        val startingProficienciesJson = jsonObject.getJSONArray("starting_proficiencies")
        for(j in 0 until startingProficienciesJson.length()) {
            val startingProficiencyJson = startingProficienciesJson.getJSONObject(j)
            startingProficiencies.add(j,  Proficiency(
                name = startingProficiencyJson.getString("name")
            ))
        }
        return startingProficiencies
    }

    private fun extractLanguages(jsonObject: JSONObject) : List<Language> {
        val languages = mutableListOf<Language>()
        val languagesJson = jsonObject.getJSONArray("languages")
        for(j in 0 until languagesJson.length()) {
            val languageJson = languagesJson.getJSONObject(j)
            languages.add(j, Language(name = languageJson.getString("name")))
        }
        return languages
    }

    private fun extractTraits(jsonObject: JSONObject) : List<Feature> {
        val traits = mutableListOf<Feature>()
        val traitsJson : JSONArray = try {
            jsonObject.getJSONArray("traits")
        } catch (e: java.lang.Exception) {
            jsonObject.getJSONArray("racial_traits")
        }

        for(j in 0 until traitsJson.length()) {
            val traitJson = traitsJson.getJSONObject(j)
            val subUrl = traitJson.getString("url")
            val traitDetailsJson = JSONObject(URL("$baseUrl$subUrl").readText())
            traits.add(j, Feature(
                name = traitDetailsJson.getString("name"),
                //For some reason the api stores descriptions inside arrays, so this is needed.
                description = traitDetailsJson.getJSONArray("desc")[0].toString(),
                level = -1,
                choiceNum = 0,
                options = null
            ))
        }
        return  traits
    }
}