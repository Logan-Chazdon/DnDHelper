package com.example.dndhelper.repository.webServices

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dndhelper.AppModule
import com.example.dndhelper.repository.dataClasses.*
import dagger.Component
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL
import kotlinx.coroutines.*
import java.lang.IllegalStateException
import com.example.dndhelper.R
import org.json.JSONException


@Component(modules = [AppModule::class])
interface Webservice {

}

class WebserviceDnD(val context: Context) : Webservice {
    private val baseUrl = "https://www.dnd5eapi.co"





    fun getItems(_items: MutableLiveData<List<Item>>) {
        GlobalScope.launch {
            _items.postValue(generateItems())
        }
    }

    private fun generateItems(): List<Item> {
        val items = mutableListOf<Item>()

        //Add all the martial weapons to the list
        items.addAll(generateWeapons(
            context.resources.openRawResource(R.raw.martial_weapons).bufferedReader().readText()
        ))

        //Add all the simple weapons to the list
        items.addAll(generateWeapons(
            context.resources.openRawResource(R.raw.simple_weapons).bufferedReader().readText()
        ))

        //Add all the armors to the list
        items.addAll(generateArmor())

        return items
    }

    private fun generateArmor(): List<Armor> {
        val dataAsString = context.resources.openRawResource(R.raw.armor).bufferedReader().readText()
        val armor = mutableListOf<Armor>()

        val armorsJson = JSONObject(dataAsString).getJSONArray("armor")
        for(index in 0 until armorsJson.length()) {
            val armorJson = armorsJson.getJSONObject(index)
            val acJson = armorJson.getJSONObject("ac")
            val cost = extractCost(armorJson.getJSONObject("cost"))
            armor.add(
                Armor(
                    name = armorJson.getString("name"),
                    desc = armorJson.getString("desc"),
                    itemRarity = armorJson.getString("item_rarity"),
                    cost = cost,
                    weight = armorJson.getInt("weight"),
                    baseAc = acJson.getInt("base"),
                    dexCap = acJson.getInt("dex_cap"),
                    stealth = armorJson.getString("stealth")
                )
            )
        }

        return armor
    }

    private fun generateWeapons(dataAsString: String) : List<Weapon> {
        val weapons = mutableListOf<Weapon>()
        val weaponsJson = JSONObject(dataAsString).getJSONArray("weapons")
        //TODO add support for magic items and attunment.
        for(weaponIndex in 0 until weaponsJson.length()) {
            val weaponJson = weaponsJson.getJSONObject(weaponIndex)


            val cost = extractCost(weaponJson.getJSONObject("cost"))
            val properties = extractProperties(weaponJson.getJSONArray("properties"))
            weapons.add(
                Weapon(
                    name = weaponJson.getString("name"),
                    desc = weaponJson.getString("desc"),
                    itemRarity = weaponJson.getString("item_rarity"),
                    cost = cost,
                    damage = weaponJson.getString("damage"),
                    damageType = weaponJson.getString("damage_type"),
                    range = weaponJson.getString("range"),
                    properties = properties
                )
            )
        }


        return weapons
    }

    private fun extractProperties(jsonArray: JSONArray): List<Property> {
        val properties = mutableListOf<Property>()
        for(index in 0 until jsonArray.length()) {
            val propertyJson = jsonArray.getJSONObject(index)
            properties.add(
                Property(
                    name = propertyJson.getString("name"),
                    desc = propertyJson.getString("desc")
                )
            )
        }
        return properties
    }

    private fun extractCost(jsonObject: JSONObject): List<Currency> {
        val cost = mutableListOf<Currency>()
        val types = mapOf(
            "Platinum pieces" to "pp",
            "Gold pieces" to "gp",
            "Electrum  pieces" to "ep",
            "Silver pieces" to "sp",
            "Copper pieces" to "cp",
        )

        for(pair in types.entries) {
            try {
                cost.add(
                    Currency(
                        name = pair.key,
                        amount = jsonObject.getInt(pair.value)
                    )
                )
            } catch (e: JSONException) { }
        }
        return cost
    }


    private fun generateSkills(): MutableMap<String, List<String>> {
        val dataAsString = context.resources.openRawResource(R.raw.skills).bufferedReader().readText()
        val abilitiesToSkills  = mutableMapOf<String, List<String>>()
        val rootJson = JSONObject(dataAsString)
        val abilitiesJson = rootJson.getJSONArray("baseStats")
        for(abilityIndex in 0 until abilitiesJson.length()) {
            val statJson = abilitiesJson.getJSONObject(abilityIndex)
            val baseStat = statJson.getString("baseStat")
            val skills = mutableListOf<String>()
            val skillsJson = statJson.getJSONArray("skills")

            for(skillIndex in 0 until skillsJson.length()) {
                val skillJson = skillsJson.getJSONObject(skillIndex)
                skills.add(
                    skillJson.getString("name")
                )
            }
            abilitiesToSkills[baseStat] = skills
        }
        return abilitiesToSkills
    }

    fun getSkills(_abilitiesToSkills: MutableLiveData<Map<String, List<String>>>) {
        GlobalScope.launch {
            _abilitiesToSkills.postValue(generateSkills())
        }
    }

    private fun extractComponents(componentsJson : JSONArray): List<String> {
        val components = mutableListOf<String>()
        for(index in 0 until componentsJson.length()) {
            val componentJson = componentsJson.getJSONObject(index)
            components.add(
                componentJson.getString("name")
            )
        }
        return components
    }

    private fun extractItemComponents(componentsJson : JSONArray): List<Item> {
        val components = mutableListOf<Item>()
        for(index in 0 until componentsJson.length()) {
            val componentJson = componentsJson.getJSONObject(index)
            components.add(
                Item(
                    name = componentJson.getString("name")
                )
            )
        }
        return components
    }

    private fun extractClasses(classesJson : JSONArray) : List<String> {
        val classes = mutableListOf<String>()
        for(index in 0 until classesJson.length()) {
            val classJson = classesJson.getJSONObject(index)
            classes.add(
                classJson.getString("name")
            )
        }
        return classes
    }


    fun getLocalSpells(_spells : MutableLiveData<List<Spell>>) {
        val dataAsString = context.resources.openRawResource(R.raw.spells).bufferedReader().readText()
        val spells = mutableListOf<Spell>()

        GlobalScope.launch {
            val rootJson = JSONObject(dataAsString)
            val spellsJson = rootJson.getJSONArray("spells")

            for(spellIndex in 0 until spellsJson.length()) {
                val spellJson = spellsJson.getJSONObject(spellIndex)
                val name = spellJson.getString("name")
                val level = spellJson.getInt("level")
                val components =
                    extractComponents(spellJson.getJSONArray("components"))
                val itemComponents =
                    extractItemComponents(spellJson.getJSONArray("item_components"))
                val school = spellJson.getString("school")
                val desc = spellJson.getString("desc")
                val range = spellJson.getInt("range")
                val area = spellJson.getString("area")
                val castingTime = spellJson.getString("casting_time")
                val duration = spellJson.getString("duration")
                val classes = extractClasses(spellJson.getJSONArray("classes"))
                val damage = spellJson.getString("damage")


                spells.add(
                    Spell(
                        name = name,
                        level = level,
                        components = components,
                        itemComponents = itemComponents,
                        school = school,
                        desc = desc,
                        range = range,
                        area = area,
                        castingTime = castingTime,
                        duration = duration,
                        classes = classes,
                        damage = damage
                    )
                )

            }
            _spells.postValue(spells)
        }
    }

    private fun generateLanguages(): MutableList<Language> {
        val dataAsString = context.resources.openRawResource(R.raw.languages).bufferedReader().readText()
        val languages = mutableListOf<Language>()

        val rootJson = JSONObject(dataAsString)
        val languagesJson = rootJson.getJSONArray("languages")

        for(languageIndex in 0 until languagesJson.length()) {
            val languageJson = languagesJson.getJSONObject(languageIndex)
            languages.add(
                Language(languageJson.getString("name"))
            )
        }
        return languages
    }

    fun getLocalLanguages(_languages : MutableLiveData<List<Language>>) {
        GlobalScope.launch {
            _languages.postValue(generateLanguages())
        }
    }


    fun getLocalBackgrounds(_backgrounds : MutableLiveData<List<Background>>) {
        val dataAsString = context.resources.openRawResource(R.raw.backgrounds).bufferedReader().readText()
        val backgrounds = mutableListOf<Background>()

        GlobalScope.launch {
            val rootJson = JSONObject(dataAsString)
            val backgroundsJson = rootJson.getJSONArray("backgrounds")
            for(backgroundIndex in 0 until backgroundsJson.length()) {
                val backgroundJson = backgroundsJson.getJSONObject(backgroundIndex)
                val name = backgroundJson.getString("name")
                val desc = backgroundJson.getString("desc")
                val proficiencies = extractProficiencies(backgroundJson.getJSONArray("proficiencies"))
                var toolProficiencies = listOf<ToolProficiency>()
                try {
                    toolProficiencies = extractToolProficiencies(backgroundJson.getJSONArray("tool_proficiencies"))
                } catch(e: JSONException) {}

                val features = extractFeatures(backgroundJson.getJSONArray("features"))
                val languages = mutableListOf<Language>()
                val languageChoices = mutableListOf<LanguageChoice>()
                try {
                   val languagesJson = backgroundJson.getJSONArray("languages")

                    for(langIndex in 0 until languagesJson.length()) {
                        val langJson = languagesJson.getJSONObject(langIndex)
                        var choose = 0
                        try {
                            choose = langJson.getInt("choose")
                        } catch (e : JSONException) { }

                        if(choose == 0) {
                            languages.add(Language(langJson.getString("name")))
                        } else {
                            val from = mutableListOf<Language>()
                            var index : String? = null
                            var langName : String? = null
                            val languageChoicesJson = langJson.getJSONArray("from")
                            for(langChoiceIndex in 0 until languageChoicesJson.length()) {
                                val choiceJson = languageChoicesJson.getJSONObject(langChoiceIndex)

                                try {
                                    index = choiceJson.getString("index")
                                    if(index == "all_languages") { //If we add more language indexes this may need to be moved into a function
                                        from.addAll(generateLanguages())
                                    }
                                } catch (e: JSONException) {

                                    try {
                                        langName = choiceJson.getString("name")
                                    } catch (e: JSONException) {
                                    }

                                    from.add(
                                        Language(
                                            name = langName,
                                            index = index
                                        )
                                    )
                                }
                            }
                            languageChoices.add(
                                LanguageChoice(
                                    name = langJson.getString("name"),
                                    choose = choose,
                                    from = from
                                )
                            )
                        }
                    }
                } catch (e: JSONException) {}


                val equipmentChoices = mutableListOf<ItemChoice>()
                val equipment = mutableListOf<Item>()
                val itemChoicesJson = backgroundJson.getJSONArray("equipment")

                for(equipmentIndex in 0 until itemChoicesJson.length()) {
                    val itemChoiceJson = itemChoicesJson.getJSONObject(equipmentIndex)
                    val choose = itemChoiceJson.getInt("choose")
                    val itemName = itemChoiceJson.getString("name")
                    if(choose != 0) {
                        val fromJson = itemChoiceJson.getJSONArray("from")
                        val from = mutableListOf<Item>()
                        for (itemIndex in 0 until fromJson.length()) {
                            val itemJson = fromJson.getJSONObject(itemIndex)
                            from.add(Item(name = itemJson.getString("name")))
                        }
                        equipmentChoices.add(ItemChoice(name = itemName, choose = choose, from = from))
                    } else {
                        val item = Item(itemChoiceJson.getString("name"))
                        equipment.add(item)
                    }
                }

                backgrounds.add(
                    Background(
                        name = name,
                        desc = desc,
                        proficiencies = proficiencies,
                        toolProficiencies = toolProficiencies,
                        features = features,
                        languages = languages,
                        languageChoices = languageChoices,
                        equipment = equipment,
                        equipmentChoices = equipmentChoices,
                    )
                )

            }
            _backgrounds.postValue(backgrounds)
        }
    }

    private fun extractToolProficiencies(proficienciesJson: JSONArray): List<ToolProficiency> {
        val proficiencies = mutableListOf<ToolProficiency>()
        for(profIndex in 0 until proficienciesJson.length()) {
            val profJson = proficienciesJson.getJSONObject(profIndex)
            proficiencies.add(
                ToolProficiency(
                    name = profJson.getString("name")
                )
            )
        }

        return proficiencies
    }

    fun getLocalRaces(_races: MutableLiveData<List<Race>>) {
        val dataAsString = context.resources.openRawResource(R.raw.races).bufferedReader().readText()
        val races = mutableListOf<Race>()
        GlobalScope.launch {
            val rootJson = JSONObject(dataAsString)
            val racesJson = rootJson.getJSONArray("races")
            for(raceIndex in 0 until racesJson.length()) {
                val raceJson = racesJson.getJSONObject(raceIndex)
                val name = raceJson.getString("name")
                val groundSpeed = raceJson.getInt("ground_speed")
                val abilityBonuses = extractLocalAbilityBonuses(raceJson.getJSONArray("ability_bonuses"))
                val alignment = raceJson.getString("alignment")
                val age = raceJson.getString("age")
                val size = raceJson.getString("size")
                val sizeDesc = raceJson.getString("size_desc")
                val startingProficiencies = extractProficiencies(raceJson.getJSONArray("proficiencies"))
                val languages = extractLangs(raceJson.getJSONArray("languages"))
                val languageDesc = raceJson.getString("language_desc")
                val traits = extractFeatures(raceJson.getJSONArray("features"))
                val subraces = mutableListOf<Subrace>() //TODO


                races.add(
                    Race(
                        name = name,
                        groundSpeed = groundSpeed,
                        abilityBonuses = abilityBonuses,
                        alignment = alignment,
                        age = age,
                        size = size,
                        sizeDesc = sizeDesc,
                        startingProficiencies = startingProficiencies,
                        languages = languages,
                        languageDesc = languageDesc,
                        traits = traits,
                        subraces = subraces.toList()
                    )
                )
            }
            _races.postValue(races)
        }
    }

    private fun extractLangs(languagesJson : JSONArray) : List<Language> {
        val langs = mutableListOf<Language>()
        for(langIndex in 0 until languagesJson.length()) {
            val langJson = languagesJson.getJSONObject(langIndex)
            langs.add(
                Language(
                    name = langJson.getString("name")
                )
            )
        }
        return langs
    }


    private fun extractProficiencies(proficienciesJson: JSONArray) : List<Proficiency> {
        val proficiencies = mutableListOf<Proficiency>()
        for(profIndex in 0 until proficienciesJson.length()) {
            val profJson = proficienciesJson.getJSONObject(profIndex)
            var index: String? = null
            try {
                index = profJson.getString("index")
                if(index == "skill_proficiencies") {
                    generateSkills().values.forEach {
                        it.forEach{ item ->
                            proficiencies.add(
                                Proficiency(
                                    name = item
                                )
                            )
                        }
                    }
                }
            }
            catch(e : JSONException) {
                proficiencies.add(
                    Proficiency(
                        name = try {profJson.getString("name")}
                        catch(e : JSONException) {null},
                        index = index
                    )
                )
            }
        }
        return proficiencies
    }

    private fun extractLocalAbilityBonuses(abilityBonusesJson: JSONArray) : List<AbilityBonus> {
        val abilityBonuses = mutableListOf<AbilityBonus>()
        for(abilityBonusIndex in 0 until abilityBonusesJson.length()) {
            val abilityBonusJson = abilityBonusesJson.getJSONObject(abilityBonusIndex)
            abilityBonuses.add(
                AbilityBonus(
                    ability = abilityBonusJson.getString("name"),
                    bonus = abilityBonusJson.getInt("bonus")
                )
            )
        }
        return abilityBonuses
    }

    private fun extractProficienciesChoices(
        json: JSONArray,
        choices: MutableList<ProficiencyChoice>
    ) {
        for(index in 0 until json.length()) {
            val profJson = json.getJSONObject(index)
            choices.add(
                ProficiencyChoice(
                    name = profJson.getString("name"),
                    choose = profJson.getInt("choose"),
                    from =
                    extractProficiencies(profJson.getJSONArray("from"))
                )
            )
        }
    }


    fun getLocalClasses(_classes: MutableLiveData<List<Class>>){
        val dataAsString = context.resources.openRawResource(R.raw.classes).bufferedReader().readText()

        val classes = mutableListOf<Class>()
        GlobalScope.launch {
            val rootJson = JSONObject(dataAsString)
            val classesJson = rootJson.getJSONArray("classes")
            for(classIndex in 0 until classesJson.length()) {
                val classJson = classesJson.getJSONObject(classIndex)
                val name = classJson.getString("name")
                val hitDie = classJson.getInt("hit_die")
                val subClasses = mutableListOf<Subclass>()
                val levelPath = extractFeatures(classJson.getJSONArray("features"))
                val proficiencyChoices = mutableListOf<ProficiencyChoice>()
                val proficiencies = mutableListOf<Proficiency>()
                val equipmentChoices: MutableList<ItemChoice> = mutableListOf()
                val equipment: MutableList<Item> = mutableListOf()
                    extractEquipmentChoices(
                        classJson.getJSONArray("equipment"),
                        equipmentChoices,
                        equipment
                    )

                try {
                extractProficienciesChoices(
                    classJson.getJSONArray("proficiency_choices"),
                    proficiencyChoices
                ) } catch (e: JSONException) {}



                classes.add(
                    Class(
                        name = name,
                        hitDie = hitDie,
                        subClasses = subClasses,
                        levelPath = levelPath,
                        proficiencyChoices = proficiencyChoices,
                        proficiencies = proficiencies,
                        equipmentChoices = equipmentChoices,
                        equipment = equipment
                    )
                )
            }
            _classes.postValue(classes)
        }
    }

    private fun extractEquipmentChoices(
        jsonArray: JSONArray,
        itemChoices: MutableList<ItemChoice>,
        items: MutableList<Item>
    ) {
        for(i in 0 until jsonArray.length()) {
            val json = jsonArray.getJSONObject(i)
            var choose = 0
            try {
                choose = json.getInt("choose")
            } catch (e: JSONException) { }

            if (choose == 0) {
                items.add(
                    Item(
                        name = json.getString("name")
                    )
                )
            } else {

                val from = mutableListOf<Item>()
                val fromJson = json.getJSONArray("from")

                for(fromIndex in 0 until fromJson.length()) {
                    val itemJson = fromJson.getJSONObject(fromIndex)
                    var name : String? = null
                    var index : String? = null

                    try {
                        name = itemJson.getString("name")
                    } catch (e: JSONException) {}

                    try {
                        index = itemJson.getString("index")
                    } catch (e: JSONException) {}

                    from.add(
                        Item(
                            name = name,
                            index = index
                        )
                    )
                }

                itemChoices.add(
                    ItemChoice(
                        name = json.getString("name"),
                        choose = choose,
                        from = from
                    )
                )
            }
        }
    }

    private fun extractFeatures(featuresJson: JSONArray) : MutableList<Feature> {
        val features = mutableListOf<Feature>()
        for(featureIndex in 0 until featuresJson.length()) {
            val featureJson = featuresJson.getJSONObject(featureIndex)
            var numOfChoices = 0
            var level = 0

            try {
                level = featureJson.getInt("level")
            } catch (e : JSONException) {}

            try {
               numOfChoices = featureJson.getInt("choose")
            } catch ( e : JSONException) {}

            val options = if(numOfChoices == 0) {
                null
            } else {
                extractFeatures(featureJson.getJSONArray("from"))
            }
            features.add(
                Feature(
                    name = featureJson.getString("name"),
                    description = featureJson.getString("desc"),
                    level = level,
                    choiceNum = numOfChoices,
                    options = options
                )
            )
        }
        return features
    }

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
            //    classes.add(Class(jsonObject.getString("name")))

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
                        var featureName = json_objectdetail.getString("name")
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
                            //The api treats ability score improvements as non choices so this is required.
                            if(featureName == "Ability Score Improvement") {
                                featureName = "Feat or Ability Score Improvement"
                                numOfChoices = 1
                                features.add(Feature("Feat", "Gain a feat", j, 1,null))//TODO replace null with all feats
                                features.add(Feature("Ability Score Improvement",
                                    "Increase one ability by two or increase two abilities by one.",
                                    j, 0,null))
                             }
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
                val size = raceJsonObject.getString("size")


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
            val ability: String = when(abilityBonusJson
                .getJSONObject("ability_score")
                .getString("name")
            ) {
                "STR" -> "Strength"
                "DEX" -> "Dexterity"
                "CON" -> "Constitution"
                "INT" -> "Intelligence"
                "WIS" -> "Wisdom"
                "CHA" -> "Charisma"
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