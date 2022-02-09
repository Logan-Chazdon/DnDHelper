package com.example.dndhelper.repository.localDataSources

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.example.dndhelper.AppModule
import com.example.dndhelper.R
import com.example.dndhelper.repository.Repository.Companion.allSpellLevels
import com.example.dndhelper.repository.dataClasses.*
import dagger.Component
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


@Component(modules = [AppModule::class])
interface LocalDataSource {
    //val _items: MutableLiveData<List<ItemInterface>>
    //val _abilitiesToSkills: MutableLiveData<Map<String, List<String>>>
    //val _spells : MutableLiveData<List<Spell>>
    //val _languages : MutableLiveData<List<Language>>
    //val _backgrounds : MutableLiveData<List<Background>>
    //val _races: MutableLiveData<List<Race>>
    //val _classes: MutableLiveData<List<Class>>
}

@Suppress("PropertyName")
@RequiresApi(Build.VERSION_CODES.P)
class LocalDataSourceImpl(val context: Context) : LocalDataSource {
     val _martialWeapons : MutableLiveData<List<Weapon>> =
        MutableLiveData()
     val _simpleWeapons : MutableLiveData<List<Weapon>> =
        MutableLiveData()
     val _armors: MutableLiveData<List<Armor>> =
        MutableLiveData()
     val _items: MediatorLiveData<List<ItemInterface>> =
        MediatorLiveData()
     val _abilitiesToSkills: MutableLiveData<Map<String, List<String>>> =
        MutableLiveData()
     val _spells : MutableLiveData<List<Spell>> =
        MutableLiveData()
     val _languages : MutableLiveData<List<Language>>  =
        MutableLiveData()
     val _feats: MutableLiveData<List<Feat>> =
        MutableLiveData()
     val _backgrounds : MutableLiveData<List<Background>> =
        MutableLiveData()
     val _races: MutableLiveData<List<Race>> =
        MutableLiveData()
     val _classes: MutableLiveData<List<Class>> =
        MutableLiveData()

    init {
        context.mainExecutor.execute {
            //Items
            generateItems()

            //Abilities
            generateSkills()

            //Spells
            generateSpells()

            //Languages
            generateLanguages()

            //Feats
            generateFeats()

            //Backgrounds
            generateBackgrounds()

            //Races
            generateRaces()

            //Classes
            generateClasses()
        }
    }

    private fun getItemByIndex(index : String) : ItemInterface? {
        _items.value?.forEach {
            if(index == it.name) {
                return it
            }
        }
        return null
    }

    //TODO implement the rest of the indexes as needed
    private fun getItemsByIndex(index: String): List<ItemInterface>? {

        when(index) {
            "musical_instruments" -> return emptyList() //TODO update
        }

       getWeaponsByIndex(index).let {
           if(it != null) {
               return it
           }
       }

       getArmorByIndex(index).let {
           if(it != null) {
               return it
           }
        }

        getItemByIndex(index).let {
            if(it != null) {
                return listOf(it)
            }
        }

        return null
    }

    private fun getWeaponsByIndex(index: String) : List<Weapon>? {
        return when(index) {
            "simple_weapons" -> _simpleWeapons.value

            "martial_weapons" -> _martialWeapons.value

            "all_weapons" -> {
                val weapons = _martialWeapons.value?.toMutableList()
                _simpleWeapons.value?.let { weapons?.addAll(it) }
                weapons
            }

            else -> null
        }
    }

    private fun getLanguagesByIndex(index: String) : List<Language>? {
        return when(index) {
            "all_languages" -> _languages.value
            else -> null
        }
    }

    private fun getArmorByIndex(index: String) : List<Armor>? {
        return when(index) {
            "all_armor" -> _armors.value
            else -> null
        }
    }

    private fun getSpellsByIndex(index: String) : List<Spell>? {
        //TODO implement me
        return when(index) {
            else -> null
        }
    }

    private fun extractResource(rootJson: JSONObject) : Resource {
        val amountJson = rootJson.getJSONObject("maxAmount")

        return Resource(
            name = rootJson.getString("name"),
            rechargeAmountType = rootJson.getString("recharge"),
            maxAmountType = amountJson.getString("type")
        )
    }

    private fun generateFeats() {
        val dataAsString = context.resources.openRawResource(R.raw.feats).bufferedReader().readText()
        val feats = mutableListOf<Feat>()
        val featsJson = JSONObject(dataAsString).getJSONArray("feats")
        for(featIndex in 0 until featsJson.length()) {
            val featJson = featsJson.getJSONObject(featIndex)
            val name = featJson.getString("name")
            val desc = featJson.getString("desc")
            val prerequisite = featJson.getString("prerequisite")
            var abilityBonusChoice : AbilityBonusChoice? = null
            val abilityBonus= null
            val features = extractFeatures(featJson.getJSONArray("features"))
            val spellChoices = mutableListOf<SpellChoice>()
            val spells = mutableListOf<Spell>()

            //TODO Ability bonuses

            //Ability bonus choice
            try {
                val abilityBonusChoiceJson = featJson.getJSONObject("ability_bonus_choice")
                val from = mutableListOf<AbilityBonus>()
                val fromJson = abilityBonusChoiceJson.getJSONArray("from")
                for(fromIndex in 0 until fromJson.length()) {
                    val abilityJson = fromJson.getJSONObject(fromIndex)
                    from.add(
                        AbilityBonus(
                            ability = abilityJson.getString("name"),
                            bonus = abilityJson.getInt("amount")
                        )
                    )
                }
                abilityBonusChoice = AbilityBonusChoice(
                    choose = abilityBonusChoiceJson.getInt("choose"),
                    from = from
                )
            } catch( e: JSONException ) {}

            //Spells
            val spellsJson = featJson.getJSONArray("spells")
            for(spellIndex in 0 until spellsJson.length()) {
                val spellJson = spellsJson.getJSONObject(spellIndex)
                val index = spellJson.getString("index")
                getSpellsByIndex(index)?.let {
                    spells.addAll(
                        it
                    )
                }
            }

            //Spell Choices
            val spellChoicesJson = featJson.getJSONArray("spell_choices")
            for(spellChoiceIndex in 0 until spellChoicesJson.length()) {
                val spellChoiceJson = spellChoicesJson.getJSONObject(spellChoiceIndex)
                val choose = spellChoiceJson.getInt("choose")
                val from = mutableListOf<Spell>()
                try {
                    val index = spellChoiceJson.getString("index")
                    getSpellsByIndex(index)?.let {
                        from.addAll(
                            it
                        )
                    }
                } catch (e: JSONException) { }
                spellChoices.add(
                    SpellChoice(
                        choose = choose,
                        from = from
                    )
                )
            }

            feats.add(
                Feat(
                    name = name,
                    desc = desc,
                    prerequisite = prerequisite,
                    abilityBonus = abilityBonus,
                    abilityBonusChoice = abilityBonusChoice,
                    features = features,
                    spellChoices = spellChoices,
                    spells = spells
                )
            )
        }
        _feats.value = feats
    }

    private fun generateItems() {
        //Anonymous function to pass to add source.
        val addData = fun(value: List<ItemInterface>){
            if(_items.value == null) {
                _items.setValue(value)
            } else {
                _items.setValue(_items.value?.plus(value))
            }
        }

        //Add all the martial weapons to items and then generate martial weapons.
        _items.addSource(_martialWeapons) {value -> addData(value) }
        _martialWeapons.value = generateWeapons(
            context.resources.openRawResource(R.raw.martial_weapons).bufferedReader().readText(),
            true
        )

        //Add all the simple weapons to items and then generate simple weapons.
        _items.addSource(_simpleWeapons) {value -> addData(value) }
        _simpleWeapons.value = generateWeapons(
            context.resources.openRawResource(R.raw.simple_weapons).bufferedReader().readText(),
            false
        )

        //Add all the armors to items and then generate armors.
        _items.addSource(_armors) {value -> addData(value) }
        _armors.value = generateArmor()
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
                    stealth = armorJson.getString("stealth"),
                    strengthPrerequisite =
                        try {armorJson.getInt("strength_prerequisite")}
                        catch(e: JSONException) {null}
                )
            )
        }

        return armor
    }

    private fun generateWeapons(dataAsString: String, isMartial: Boolean) : List<Weapon> {
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
                    properties = properties,
                    isMartial = isMartial
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

    private fun extractCost(jsonObject: JSONObject): MutableMap<String, Currency> {
        val cost = mutableMapOf<String, Currency>()
        val types = mapOf(
            "Platinum pieces" to "pp",
            "Gold pieces" to "gp",
            "Electrum pieces" to "ep",
            "Silver pieces" to "sp",
            "Copper pieces" to "cp",
        )

        for(pair in types.entries) {
            try {
                cost[pair.value] = (
                    Currency(
                        name = pair.key,
                        amount = jsonObject.getInt(pair.value),
                        weight = 0 //TODO
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
        _abilitiesToSkills.value = abilitiesToSkills
        return abilitiesToSkills
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


    fun generateSpells() {
        val dataAsString = context.resources.openRawResource(R.raw.spells).bufferedReader().readText()
        val spells = mutableListOf<Spell>()

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
            val range = spellJson.getString("range")
            val area = spellJson.getString("area")
            val castingTime = spellJson.getString("casting_time")
            val duration = spellJson.getString("duration")
            val classes = extractClasses(spellJson.getJSONArray("classes"))
            val damage = spellJson.getString("damage")
            val ritual = try {
                spellJson.getBoolean("ritual")
            } catch (e: JSONException) {
                false
            }

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
                    damage = damage,
                    isRitual = ritual
                )
            )
        }
        _spells.value = spells
    }

    private fun generateLanguages(){
            val dataAsString =
                context.resources.openRawResource(R.raw.languages).bufferedReader().readText()
            val languages = mutableListOf<Language>()

            val rootJson = JSONObject(dataAsString)
            val languagesJson = rootJson.getJSONArray("languages")

            for (languageIndex in 0 until languagesJson.length()) {
                val languageJson = languagesJson.getJSONObject(languageIndex)
                languages.add(
                    Language(languageJson.getString("name"))
                )
            _languages.value = languages
        }
    }




    private fun generateBackgrounds() {
        val dataAsString = context.resources.openRawResource(R.raw.backgrounds).bufferedReader().readText()
        val backgrounds = mutableListOf<Background>()


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
                                        _languages.value?.let { from.addAll(it) }
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
            val equipment = mutableListOf<ItemInterface>()
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
                    try {
                        val index = itemChoiceJson.getString("index")
                        if(index == "gold") {
                            equipment.add(
                                Currency(
                                    name = "${itemChoiceJson.getInt("value")} gold pieces",
                                    amount = itemChoiceJson.getInt("value")
                                )
                            )
                        }
                    } catch (e : JSONException) {
                        val item = Item(itemChoiceJson.getString("name"))
                        equipment.add(item)
                    }
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
        _backgrounds.value = backgrounds
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

    fun generateRaces() {
        val dataAsString = context.resources.openRawResource(R.raw.races).bufferedReader().readText()
        val races = mutableListOf<Race>()
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
        _races.value = races
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
                            if(!item.contains("Saving")) {
                                proficiencies.add(
                                    Proficiency(
                                        name = item
                                    )
                                )
                            }
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


    fun generateClasses(){
        val dataAsString = context.resources.openRawResource(R.raw.classes).bufferedReader().readText()

        val classes = mutableListOf<Class>()
        val rootJson = JSONObject(dataAsString)
        val classesJson = rootJson.getJSONArray("classes")
        for(classIndex in 0 until classesJson.length()) {
            val classJson = classesJson.getJSONObject(classIndex)
            val name = classJson.getString("name")
            val hitDie = classJson.getInt("hit_die")
            val subClasses = mutableListOf<Subclass>()
            val levelPath = extractFeatures(classJson.getJSONArray("features"))
            val proficiencyChoices = mutableListOf<ProficiencyChoice>()
            val proficiencies = extractProficiencies(classJson.getJSONArray("proficiencies"))
            val equipmentChoices: MutableList<ItemChoice> = mutableListOf()
            val equipment: MutableList<ItemInterface> = mutableListOf()
            extractEquipmentChoices(
                classJson.getJSONArray("equipment"),
                equipmentChoices,
                equipment
            )
            try {
                extractProficienciesChoices(
                    classJson.getJSONArray("proficiency_choices"),
                    proficiencyChoices
                )
            } catch (e: JSONException) {}

            val subclassesJson = classJson.getJSONArray("subclasses")
            for(subclassIndex in 0 until subclassesJson.length()) {
                val subclassJson = subclassesJson.getJSONObject(subclassIndex)
                subClasses.add(
                    Subclass(
                        name = subclassJson.getString("name"),
                        features = extractFeatures(subclassJson.getJSONArray("features")),
                        spells = listOf() //TODO fill out this list
                    )
                )
            }


            val spellCastingJson = classJson.getJSONObject("spell_casting")
            val spellCastingType = spellCastingJson.getDouble("type")
            val spellCasting : SpellCasting? = if(spellCastingType == 0.0) {
                null
            } else {
                SpellCasting(
                    type = spellCastingType,
                    castingAbility = spellCastingJson.getString("casting_ability"),
                    preparationModMultiplier = try {
                        spellCastingJson.getDouble("preparation_mod_multiplier")
                    } catch (e: JSONException) {
                        null
                    },
                    prepareFrom = try {
                        spellCastingJson.getString("prepare_from")
                    } catch (e: JSONException) {
                        null
                    },
                    spellsKnown = try {
                        val spellsKnownJson = spellCastingJson.getJSONArray("spells_known")
                        val result = mutableListOf<Int>()
                        for(i in 0 until spellsKnownJson.length()) {
                            result.add(spellsKnownJson.getInt(i))
                        }
                        result
                    } catch (e: JSONException) {
                        null
                    },
                    cantripsKnown = try {
                        val spellsKnownJson = spellCastingJson.getJSONArray("cantrips_known")
                        val result = mutableListOf<Int>()
                        for(i in 0 until spellsKnownJson.length()) {
                            result.add(spellsKnownJson.getInt(i))
                        }
                        result
                    } catch (e: JSONException) {
                        null
                    },
                    spellSlotsByLevel = spellCastingJson.getJSONArray("spell_slots").let {
                        val result = mutableListOf<List<Resource>>()
                        for(levelIndex in 0 until it.length()) {
                            val level = mutableListOf<Resource>()
                            val levelJson = it.getJSONArray(levelIndex)
                            for(slotIndex in 0 until levelJson.length()) {
                                val slot = levelJson.getInt(slotIndex)
                                level.add(
                                    Resource(
                                        name = allSpellLevels[slotIndex].second,
                                        currentAmount = slot,
                                        maxAmountType = slot.toString(),
                                        rechargeAmountType = "full"
                                    )
                                )
                            }
                            result.add(level.toList())
                        }
                        result
                    }
                )
            }

             classes.add(
                 Class(
                     name = name,
                     hitDie = hitDie,
                     subClasses = subClasses,
                     levelPath = levelPath,
                     proficiencyChoices = proficiencyChoices,
                     proficiencies = proficiencies,
                     equipmentChoices = equipmentChoices,
                     equipment = equipment,
                     spellCasting = spellCasting,
                     subclassLevel = classJson.getInt("subclass_level"),
                     startingGoldD4s = classJson.getInt("starting_gold_d4s")
                 )
             )
        }
        _classes.value = classes
    }

    private fun extractEquipmentChoices(
        jsonArray: JSONArray,
        itemChoices: MutableList<ItemChoice>,
        items: MutableList<ItemInterface>
    ) {
        for(i in 0 until jsonArray.length()) {
            val json = jsonArray.getJSONObject(i)
            var choose = 0
            try {
                choose = json.getInt("choose")
            } catch (e: JSONException) { }

            if (choose == 0) {
                try {
                    val index = json.getString("index")
                    getItemByIndex(index)?.let { items.add(it) }
                } catch (e: JSONException) {
                    items.add(
                        Item(
                            name = json.getString("name")
                        )
                    )
                }
            } else {

                val from = mutableListOf<ItemInterface>()
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
                        getItemsByIndex(index)?.let { from.addAll(it) }

                    } catch (e: JSONException) {
                        from.add(
                            Item(
                                name = name,
                            )
                        )
                    }


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

    private fun extractFeatures(featuresJson: JSONArray): MutableList<Feature> {
        val features = mutableListOf<Feature>()
        for (featureIndex in 0 until featuresJson.length()) {
            val featureJson = featuresJson.getJSONObject(featureIndex)
            var numOfChoices = 0
            val level = try {
                featureJson.getInt("level")
            } catch (e: JSONException) { 0 }

            var options : MutableList<Feature>? = null
            //If we have an index construct a list from that otherwise just make it normally.
            try {
                when(featureJson.getString("index")) {
                    "proficiencies" -> {
                        _abilitiesToSkills.value!!.forEach {
                            it.value.forEach { skill ->
                                //Don't add saving throws
                                if(!skill.lowercase().contains("throw")) {
                                    features.add(Feature(
                                        name = skill,
                                        description = "",
                                        level = 0,
                                        choiceNum = 0,
                                        options = null,
                                        prerequisite = Prerequisite(
                                            proficiency = Proficiency(
                                                name = skill
                                            )
                                        )
                                    ))
                                }
                            }
                        }
                    }
                    "all_spells" -> {
                        val bound = try {
                            featureJson.getInt("bound")
                        } catch(e: JSONException) {
                            null
                        }
                        _spells.value?.forEach { spell ->
                            if(spell.level <= bound ?: 10) {
                                features.add(
                                    Feature(
                                        name = spell.name,
                                        spells = listOf(spell),
                                        description = "",
                                        options = null,
                                    )
                                )
                            }
                        }
                    }
                    else -> throw JSONException("Invalid index")
                }

            } catch (e: JSONException) {
                try {
                    numOfChoices = featureJson.getInt("choose")
                } catch (e: JSONException) { }

                options = if (numOfChoices == 0) {
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
        }
        return features
    }
}