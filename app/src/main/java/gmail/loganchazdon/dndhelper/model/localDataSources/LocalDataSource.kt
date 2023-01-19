package gmail.loganchazdon.dndhelper.model.localDataSources

import android.content.Context
import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import dagger.Component
import gmail.loganchazdon.dndhelper.AppModule
import gmail.loganchazdon.dndhelper.R
import gmail.loganchazdon.dndhelper.model.*
import gmail.loganchazdon.dndhelper.model.database.DatabaseDao
import gmail.loganchazdon.dndhelper.model.junctionEntities.*
import gmail.loganchazdon.dndhelper.model.repositories.Repository.Companion.allSpellLevels
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

@Suppress("PropertyName")
@Component(modules = [AppModule::class])
interface LocalDataSource {
    fun getItems(items: MutableLiveData<List<ItemInterface>>): MutableLiveData<List<ItemInterface>>
    fun getAbilitiesToSkills(abilitiesToSKills: MutableLiveData<Map<String, List<String>>>): MutableLiveData<Map<String, List<String>>>
    fun getLanguages(languages: MutableLiveData<List<Language>>): MutableLiveData<List<Language>>
    fun getMetaMagics(metamagics: MutableLiveData<List<Metamagic>>): MutableLiveData<List<Metamagic>>
    fun getFeats(feats: MutableLiveData<List<Feat>>): MutableLiveData<List<Feat>>
    fun getArmors(armors: MutableLiveData<List<Armor>>): MutableLiveData<List<Armor>>
    fun getMiscItems(miscItems: MutableLiveData<List<ItemInterface>>): MutableLiveData<List<ItemInterface>>
    fun getMartialWeapons(martialWeapons: MutableLiveData<List<Weapon>>): MutableLiveData<List<Weapon>>
    fun getInfusions(infusions: MutableLiveData<List<Infusion>>): MutableLiveData<List<Infusion>>
    fun getSimpleWeapons(simpleWeapons: MutableLiveData<List<Weapon>>): MutableLiveData<List<Weapon>>
    fun getInvocations(invocations: MutableLiveData<List<Invocation>>): MutableLiveData<List<Invocation>>
}


class LocalDataSourceImpl @Inject constructor(val context: Context, val dao: DatabaseDao) :
    LocalDataSource {
    private val scope = CoroutineScope(Dispatchers.IO + Job())
    private val _infusions: MutableLiveData<List<Infusion>> =
        MutableLiveData()
    private val _invocations: MutableLiveData<List<Invocation>> =
        MutableLiveData()
    private val _martialWeapons: MutableLiveData<List<Weapon>> =
        MutableLiveData()
    private val _simpleWeapons: MutableLiveData<List<Weapon>> =
        MutableLiveData()
    private val _miscItems: MutableLiveData<List<ItemInterface>> =
        MutableLiveData()
    private val _armors: MutableLiveData<List<Armor>> =
        MutableLiveData()
    private val _items: MediatorLiveData<List<ItemInterface>> =
        MediatorLiveData()
    private val _abilitiesToSkills: MutableLiveData<Map<String, List<String>>> =
        MutableLiveData()
    private val _languages: MutableLiveData<List<Language>> =
        MutableLiveData()
    private val _feats: MutableLiveData<List<Feat>> =
        MutableLiveData()
    private val _metaMagics: MutableLiveData<List<Metamagic>> =
        MutableLiveData()
    private val _maneuvers: MutableLiveData<List<Feature>> =
        MutableLiveData()
    private val _fightingStyles: MutableLiveData<List<Feature>> =
        MutableLiveData()

    private val instrumentIndexes = mutableListOf(
        "Bagpipes",
        "Drum",
        "Dulcimer",
        "Flute",
        "Lute",
        "Lyre",
        "Horn",
        "Pan Flute",
        "Shawm",
        "Viol",
        "Hurdy-Gurdy",
        "Sackbut",
        "Whistle-Stick",
        "Harp",
        "Tambourine",
        "Erhu",
        "Hulusi",
        "Udu",
        "Maracas",
        "Gong",
        "Wargong"
    )
    private val artisansToolIndexes = mutableListOf(
        "Alchemist's supplies",
        "Brewer's supplies",
        "Calligrapher's Supplies",
        "Carpenter's tools",
        "Cartographer's tools",
        "Cobbler's tools",
        "Cook's utensils",
        "Glassblower's tools",
        "Jeweler's tools",
        "Leatherworker's tools",
        "Mason's tools",
        "Painter's supplies",
        "Potter's tools",
        "Smith's tools",
        "Tinker's tools",
        "Weaver's tools",
        "Woodcarver's tools"
    )
    private val gamingSetIndexes = mutableListOf(
        "Dice set",
        "Dragonchess set",
        "Playing card set",
        "Three-Dragon Ante set"
    )

    override fun getItems(items: MutableLiveData<List<ItemInterface>>): MutableLiveData<List<ItemInterface>> =
        _items

    override fun getAbilitiesToSkills(abilitiesToSKills: MutableLiveData<Map<String, List<String>>>): MutableLiveData<Map<String, List<String>>> =
        _abilitiesToSkills


    override fun getLanguages(languages: MutableLiveData<List<Language>>): MutableLiveData<List<Language>> =
        _languages

    override fun getMetaMagics(metamagics: MutableLiveData<List<Metamagic>>): MutableLiveData<List<Metamagic>> =
        _metaMagics

    override fun getFeats(feats: MutableLiveData<List<Feat>>): MutableLiveData<List<Feat>> = _feats
    override fun getArmors(armors: MutableLiveData<List<Armor>>): MutableLiveData<List<Armor>> =
        _armors

    override fun getMiscItems(miscItems: MutableLiveData<List<ItemInterface>>): MutableLiveData<List<ItemInterface>> =
        _miscItems

    override fun getMartialWeapons(martialWeapons: MutableLiveData<List<Weapon>>): MutableLiveData<List<Weapon>> =
        _martialWeapons

    override fun getInfusions(infusions: MutableLiveData<List<Infusion>>): MutableLiveData<List<Infusion>> =
        _infusions

    override fun getSimpleWeapons(simpleWeapons: MutableLiveData<List<Weapon>>): MutableLiveData<List<Weapon>> =
        _simpleWeapons

    override fun getInvocations(invocations: MutableLiveData<List<Invocation>>): MutableLiveData<List<Invocation>> =
        _invocations

    private val classNameToId = mapOf(
        "artificer" to 1,
        "barbarian" to 2,
        "bard" to 3,
        "cleric" to 4,
        "druid" to 5,
        "fighter" to 6,
        "monk" to 7,
        "paladin" to 8,
        "ranger" to 9,
        "rogue" to 10,
        "sorcerer" to 11,
        "warlock" to 12,
        "wizard" to 13
    )

    init {
        //Items
        generateItems()

        //Abilities
        generateSkills()

        //Spells
        updateSpells()

        //Languages
        generateLanguages()

        //Infusions
        generateInfusions()

        //Invocations
        generateInvocations()

        //Metamagic
        generateMetaMagic()

        //Maneuvers
        generateManeuvers()

        //Fighting Styles
        generateFightingStyles()

        //Feats
        generateFeats()

        //Backgrounds
        updateBackgrounds()

        //Races
        updateRaces()

        //Classes
        updateClasses()

    }

    private fun generateFightingStyles() {
        val dataAsString =
            context.resources.openRawResource(R.raw.fighting_styles).bufferedReader().readText()
        val fightingStylesJson = JSONObject(dataAsString).getJSONArray("fighting_styles")
        //_fightingStyles.value = extractFeatures(
        //    fightingStylesJson
        //) TODO
    }

    private fun generateMetaMagic() {
        val metamagic = mutableListOf<Metamagic>()
        val dataAsString =
            context.resources.openRawResource(R.raw.metamagic).bufferedReader().readText()
        val metamagicsJson = JSONObject(dataAsString).getJSONArray("metamagic")
        for (index in 0 until metamagicsJson.length()) {
            val metamagicJson = metamagicsJson.getJSONObject(index)
            metamagic.add(
                Metamagic(
                    name = metamagicJson.getString("name"),
                    desc = metamagicJson.getString("desc")
                )
            )
        }
        _metaMagics.value = metamagic
    }

    private fun generateManeuvers() {
        val dataAsString =
            context.resources.openRawResource(R.raw.maneuvers).bufferedReader().readText()
        val maneuvers = mutableListOf<Feature>()
        val maneuversJson = JSONObject(dataAsString).getJSONArray("maneuvers")
        for (index in 0 until maneuversJson.length()) {
            val maneuverJson = maneuversJson.getJSONObject(index)
            maneuvers.add(
                Feature(
                    name = maneuverJson.getString("name"),
                    description = maneuverJson.getString("desc"),
                    grantedAtLevel = 0,
                )
            )
        }
        _maneuvers.value = maneuvers
    }

    private fun generateInvocations() {
        val dataAsString =
            context.resources.openRawResource(R.raw.invocations).bufferedReader().readText()
        val invocations = mutableListOf<Invocation>()
        val invocationsJson = JSONObject(dataAsString).getJSONArray("invocations")
        for (index in 0 until invocationsJson.length()) {
            val invocationJson = invocationsJson.getJSONObject(index)
            invocations.add(
                Invocation(
                    name = invocationJson.getString("name"),
                    desc = invocationJson.getString("desc"),
                    prerequisite = try {
                        extractPrerequisite(invocationJson.getJSONObject("prerequisite"))
                    } catch (e: JSONException) {
                        null
                    }
                )
            )
        }
        _invocations.value = invocations
    }

    private fun extractPrerequisite(jsonObject: JSONObject): Prerequisite {
        val level = try {
            jsonObject.getInt("level")
        } catch (e: JSONException) {
            null
        }

        val feature = try {
            jsonObject.getString("feature")
        } catch (e: JSONException) {
            null
        }

        val spell = try {
            jsonObject.getString("spell")
        } catch (e: JSONException) {
            null
        }

        val hasSpells = try {
            jsonObject.getBoolean("has_spells")
        } catch (e: JSONException) {
            null
        }

        val isCaster = try {
            jsonObject.getBoolean("is_caster")
        } catch (e: JSONException) {
            null
        }

        val stats = mutableMapOf<String, Int>()
        val statNames = listOf(
            "Strength",
            "Dexterity",
            "Constitution",
            "Intelligence",
            "Wisdom",
            "Charisma"
        )
        for (name in statNames) {
            try {
                stats[name.substring(0, 3)] = jsonObject.getInt(name)
            } catch (e: JSONException) {
            }
        }

        return Prerequisite(
            level = level,
            feature = feature,
            spell = spell,
            hasSpells = hasSpells,
            isCaster = isCaster,
            stats = stats
        )
    }

    private fun generateInfusions() {
        val dataAsString =
            context.resources.openRawResource(R.raw.infusions).bufferedReader().readText()
        val infusions = mutableListOf<Infusion>()
        val infusionsJson = JSONObject(dataAsString).getJSONArray("infusions")
        for (index in 0 until infusionsJson.length()) {
            val infusionJson = infusionsJson.getJSONObject(index)
            infusions.add(extractInfusion(infusionJson))
        }
        _infusions.value = infusions
    }

    private fun extractInfusion(infusionJson: JSONObject): Infusion {
        val options = try {
            val fromJson = infusionJson.getJSONArray("from")
            val result = mutableListOf<Feature>()
            for (index in 0 until fromJson.length()) {
                val infusion = extractInfusion(fromJson.getJSONObject(index))
                result.add(
                    Feature(
                        name = infusion.name,
                        description = infusion.desc,
                        grantedAtLevel = infusion.grantedAtLevel,
                        maxTimesChosen = infusion.maxTimesChosen,
                        infusion = infusion,
                        featureId = fromJson.getJSONObject(index).getInt("id")
                    )
                )
            }
            result
        } catch (e: JSONException) {
            null
        }

        return Infusion(
            choose = try {
                infusionJson.getInt("choose")
            } catch (e: JSONException) {
                0
            },
            options = options,
            maxTimesChosen = try {
                infusionJson.getInt("max_times_chosen")
            } catch (e: JSONException) {
                null
            },
            grantedAtLevel = try {
                infusionJson.getInt("level")
            } catch (e: JSONException) {
                0
            },
            name = infusionJson.getString("name"),
            desc = infusionJson.getString("desc"),
            charges = null, //TODO implement me
            atkDmgBonus = try {
                extractScalingBonus(infusionJson.getJSONArray("atk_dmg_bonus"))
            } catch (e: JSONException) {
                null
            },
            acBonus = try {
                extractScalingBonus(infusionJson.getJSONArray("ac_bonus"))
            } catch (e: JSONException) {
                null
            },
            attuned = try {
                infusionJson.getBoolean("requires_attunement")
            } catch (e: JSONException) {
                false
            },
            targetItemFilter = try {
                extractTargetItemFilter(infusionJson.getJSONObject("target_item_filter"))
            } catch (e: JSONException) {
                null
            },
            id = infusionJson.getInt("id")
        )
    }

    private fun extractTargetItemFilter(json: JSONObject): TargetItemFilter {
        val types = try {
            val typesJson = json.getJSONArray("types")
            val result = mutableListOf<String>()
            for (index in 0 until typesJson.length()) {
                result.add(typesJson.getString(index))
            }
            result
        } catch (e: JSONException) {
            null
        }

        val properties = try {
            extractProperties(json.getJSONArray("properties"))
        } catch (e: JSONException) {
            null
        }

        val minimumCost = try {
            extractCost(json.getJSONObject("cost"))
        } catch (e: JSONException) {
            null
        }

        return TargetItemFilter(
            itemTypes = types,
            minimumCost = minimumCost,
            properties = properties
        )
    }

    private fun extractScalingBonus(json: JSONArray): ScalingBonus {
        val bonuses = mutableMapOf<Int, Int>()
        for (index in 0 until json.length()) {
            val bonusJson = json.getJSONObject(index)
            bonuses[bonusJson.getInt("prerequisite")] = bonusJson.getInt("bonus")
        }
        return ScalingBonus(bonuses = bonuses)
    }

    private fun getItemByIndex(index: String): ItemInterface? {
        _items.value?.forEach {
            if (index.lowercase() == it.name?.lowercase()) {
                return it
            }
        }
        return null
    }

    //TODO implement the rest of the indexes as needed
    private fun getItemsByIndex(index: String): List<ItemInterface>? {

        when (index) {
            "simple_melee_weapons" -> {
                return _simpleWeapons.value!!.filter { it.range == "5ft" }
            }
            "gaming_sets" -> {
                val result = mutableListOf<ItemInterface>()
                gamingSetIndexes.forEach {
                    result.add(getItemByIndex(it)!!)
                }
                return result
            }
            "artisans_tools" -> {
                val result = mutableListOf<ItemInterface>()
                artisansToolIndexes.forEach {
                    result.add(getItemByIndex(it)!!)
                }
                return result
            }
            "musical_instruments" -> return mutableListOf<ItemInterface>().run {
                instrumentIndexes.forEach {
                    getItemByIndex(it)?.let { item -> this.add(item) }
                }
                this
            }
        }

        getWeaponsByIndex(index).let {
            if (it != null) {
                return it
            }
        }

        getArmorByIndex(index).let {
            if (it != null) {
                return it
            }
        }

        getItemByIndex(index).let {
            if (it != null) {
                return listOf(it)
            }
        }

        return null
    }

    private fun getWeaponsByIndex(index: String): List<Weapon>? {
        return when (index) {
            "simple_weapons" -> _simpleWeapons.value

            "martial_weapons" -> _martialWeapons.value

            "all_weapons" -> {
                val weapons = _martialWeapons.value?.toMutableList()
                _simpleWeapons.value?.let { weapons?.addAll(it) }
                weapons
            }

            else -> {
                var result: List<Weapon>? = null
                val weapons = mutableListOf<Weapon>()
                _martialWeapons.value?.let { weapons.addAll(it) }
                _simpleWeapons.value?.let { weapons.addAll(it) }
                for (item in weapons) {
                    if (item.name?.lowercase() == index.lowercase()) {
                        result = listOf(item)
                        break
                    }
                }
                result
            }
        }
    }

    private fun getLanguagesByIndex(index: String): List<Language>? {
        return when (index) {
            "all_languages" -> _languages.value
            else -> null
        }
    }

    private fun getArmorByIndex(index: String): List<Armor>? {
        return when (index) {
            "all_armor" -> _armors.value
            else -> {
                var result: List<Armor>? = null
                for (item in _armors.value!!) {
                    if (item.name?.lowercase() == index.lowercase()) {
                        result = listOf(item)
                        break
                    }
                }
                result
            }
        }
    }

    private fun getSpellsByIndex(index: String): List<Spell>? {
        when (index) {
            else -> {
                /* _spells.value?.forEach {
                     if (index.lowercase().trim() == it.name.lowercase().trim()) {
                         return listOf(it)
                     }
                 } TODO*/
                return null
            }
        }
    }

    private fun extractResource(rootJson: JSONObject): Resource {
        val amountJson = rootJson.getJSONObject("maxAmount")

        return Resource(
            name = rootJson.getString("name"),
            rechargeAmountType = rootJson.getString("recharge"),
            maxAmountType = amountJson.getString("type")
        )
    }

    private fun generateFeats() {
        val dataAsString =
            context.resources.openRawResource(R.raw.feats).bufferedReader().readText()
        val feats = mutableListOf<Feat>()
        val featsJson = JSONObject(dataAsString).getJSONArray("feats")
        for (featIndex in 0 until featsJson.length()) {
            val featJson = featsJson.getJSONObject(featIndex)
            val name = featJson.getString("name")
            val desc = featJson.getString("desc")
            val prerequisite = try {
                extractPrerequisite(
                    featJson.getJSONObject("prerequisite")
                )
            } catch (e: JSONException) {
                null
            }

            val features = extractFeatures(featJson.getJSONArray("features"))


            val abilityBonuses = mutableListOf<AbilityBonus>()
            var abilityBonusChoice: AbilityBonusChoice? = null
            try {
                abilityBonusChoice =
                    extractAbilityBonuses(featJson.getJSONArray("ability_bonuses"), abilityBonuses)
            } catch (e: JSONException) {
            }


            feats.add(
                Feat(
                    name = name,
                    desc = desc,
                    prerequisite = prerequisite,
                    abilityBonuses = abilityBonuses,
                    abilityBonusChoice = abilityBonusChoice,
                    //features = features TODO
                )
            )
        }
        _feats.value = feats
    }

    private fun generateItems() {
        //Anonymous function to pass to add source.
        val addData = fun(value: List<ItemInterface>) {
            if (_items.value == null) {
                _items.setValue(value)
            } else {
                _items.setValue(_items.value?.plus(value))
            }
        }

        //Add all the martial weapons to items and then generate martial weapons.
        _items.addSource(_martialWeapons) { value -> addData(value) }
        _martialWeapons.value = generateWeapons(
            context.resources.openRawResource(R.raw.martial_weapons).bufferedReader().readText(),
            true
        )

        //Add all the simple weapons to items and then generate simple weapons.
        _items.addSource(_simpleWeapons) { value -> addData(value) }
        _simpleWeapons.value = generateWeapons(
            context.resources.openRawResource(R.raw.simple_weapons).bufferedReader().readText(),
            false
        )

        //Add all the armors to items and then generate armors.
        _items.addSource(_armors) { value -> addData(value) }
        _armors.value = generateArmor()

        _items.addSource(_miscItems) { value -> addData(value) }
        _miscItems.value = generateMiscItems()

        //This is needed because if _items is not observed its data will not be directly accessible until it is.
        //So in order to check it for item indexes we need an empty observer.
        _items.observeForever {

        }
    }

    private fun generateMiscItems(): List<ItemInterface> {
        val dataAsString =
            context.resources.openRawResource(R.raw.misc_items).bufferedReader().readText()
        val result = mutableListOf<ItemInterface>()
        val itemsJson = JSONObject(dataAsString).getJSONArray("items")
        for (index in 0 until itemsJson.length()) {
            val itemJson = itemsJson.getJSONObject(index)
            //Try to log the item causing an issue.
            //Just rethrows the exception after logging.
            try {
                val cost = try {
                    extractCost(itemJson.getJSONObject("cost"))
                } catch (e: JSONException) {
                    null
                }

                val weight = try {
                    itemJson.getInt("weight")
                } catch (e: JSONException) {
                    null
                }

                val itemRarity = try {
                    itemJson.getString("item_rarity")
                } catch (e: JSONException) {
                    null
                }

                val name = itemJson.getString("name")
                val desc = itemJson.getString("desc")
                when (itemJson.getString("type")) {
                    "shield" -> {
                        result.add(
                            Shield(
                                name = name,
                                desc = desc,
                                itemRarity = itemRarity,
                                cost = cost,
                                weight = weight,
                                acBonus = itemJson.getInt("ac_bonus"),
                                charges = try {
                                    extractResource(itemJson.getJSONObject("charges"))
                                } catch (e: Exception) {
                                    null
                                },
                                index = null
                            )
                        )
                    }
                    "weapon" -> {
                        val properties = try {
                            extractProperties(itemJson.getJSONArray("properties"))
                        } catch (e: JSONException) {
                            listOf()
                        }
                        val range = try {
                            itemJson.getString("range")
                        } catch (e: JSONException) {
                            null
                        }
                        result.add(
                            Weapon(
                                name = name,
                                desc = desc,
                                itemRarity = itemRarity,
                                cost = cost,
                                weight = weight,
                                damage = itemJson.getString("damage"),
                                damageType = itemJson.getString("damage_type"),
                                range = range,
                                properties = properties,
                                proficiency = try {
                                    itemJson.getString("proficiency")
                                } catch (e: JSONException) {
                                    "Improvised weapons"
                                }
                            )
                        )
                    }
                    "item" -> {
                        result.add(
                            Item(
                                name = name,
                                desc = desc,
                                weight = weight,
                                cost = cost
                            )
                        )
                    }
                }
            } catch (e: JSONException) {
                Log.e("Error", "generateMiscItems: $itemJson")
                throw e
            }
        }

        return result
    }

    private fun generateArmor(): List<Armor> {
        val dataAsString =
            context.resources.openRawResource(R.raw.armor).bufferedReader().readText()
        val armor = mutableListOf<Armor>()

        val armorsJson = JSONObject(dataAsString).getJSONArray("armor")
        for (index in 0 until armorsJson.length()) {
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
                    try {
                        armorJson.getInt("strength_prerequisite")
                    } catch (e: JSONException) {
                        null
                    }
                )
            )
        }

        return armor
    }

    private fun generateWeapons(dataAsString: String, isMartial: Boolean): List<Weapon> {
        val weapons = mutableListOf<Weapon>()
        val weaponsJson = JSONObject(dataAsString).getJSONArray("weapons")
        //TODO add support for magic items and attunment.
        for (weaponIndex in 0 until weaponsJson.length()) {
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
                    range = try {
                        weaponJson.getString("range")
                    } catch (e: JSONException) {
                        null
                    },
                    properties = properties,
                    proficiency = if (isMartial) {
                        "Martial weapons"
                    } else {
                        "Simple weapons"
                    }
                )
            )
        }


        return weapons
    }

    private fun extractProperties(jsonArray: JSONArray): List<Property> {
        val properties = mutableListOf<Property>()
        for (index in 0 until jsonArray.length()) {
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

        for (pair in types.entries) {
            try {
                cost[pair.value] = (
                        Currency(
                            name = pair.key,
                            amount = jsonObject.getInt(pair.value),
                            weight = 0 //TODO
                        )
                        )
            } catch (e: JSONException) {
            }
        }
        return cost
    }


    private fun generateSkills() {
        val dataAsString =
            context.resources.openRawResource(R.raw.skills).bufferedReader().readText()
        val abilitiesToSkills = mutableMapOf<String, List<String>>()
        val rootJson = JSONObject(dataAsString)
        val abilitiesJson = rootJson.getJSONArray("baseStats")
        for (abilityIndex in 0 until abilitiesJson.length()) {
            val statJson = abilitiesJson.getJSONObject(abilityIndex)
            val baseStat = statJson.getString("baseStat")
            val skills = mutableListOf<String>()
            val skillsJson = statJson.getJSONArray("skills")

            for (skillIndex in 0 until skillsJson.length()) {
                val skillJson = skillsJson.getJSONObject(skillIndex)
                skills.add(
                    skillJson.getString("name")
                )
            }
            abilitiesToSkills[baseStat] = skills
        }
        _abilitiesToSkills.value = abilitiesToSkills
    }


    private fun extractComponents(componentsJson: JSONArray): List<String> {
        val components = mutableListOf<String>()
        for (index in 0 until componentsJson.length()) {
            val componentJson = componentsJson.getJSONObject(index)
            components.add(
                componentJson.getString("name")
            )
        }
        return components
    }

    private fun extractItemComponents(componentsJson: JSONArray): List<Item> {
        val components = mutableListOf<Item>()
        for (index in 0 until componentsJson.length()) {
            val componentJson = componentsJson.getJSONObject(index)
            components.add(
                Item(
                    name = componentJson.getString("name")
                )
            )
        }
        return components
    }

    private fun extractClasses(classesJson: JSONArray): List<String> {
        val classes = mutableListOf<String>()
        for (index in 0 until classesJson.length()) {
            val classJson = classesJson.getJSONObject(index)
            classes.add(
                classJson.getString("name")
            )
        }
        return classes
    }


    private fun updateSpells() {
        val dataAsString =
            context.resources.openRawResource(R.raw.spells).bufferedReader().readText()

        val rootJson = JSONObject(dataAsString)
        val spellsJson = rootJson.getJSONArray("spells")

        for (spellIndex in 0 until spellsJson.length()) {
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

            scope.launch {
                val id = spellJson.getInt("id")
                dao.insertSpell(
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
                        isRitual = ritual,
                    ).run {
                        this.id = id
                        this
                    })

                classes.forEach {
                    dao.insertClassSpellCrossRef(
                        ClassSpellCrossRef(
                            classId = classNameToId[it.lowercase()]!!,
                            spellId = id
                        )
                    )
                }
            }
        }
    }

    private fun generateLanguages() {
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


    private fun updateBackgrounds() {
        val dataAsString =
            context.resources.openRawResource(R.raw.backgrounds).bufferedReader().readText()

        val rootJson = JSONObject(dataAsString)
        val backgroundsJson = rootJson.getJSONArray("backgrounds")
        for (backgroundIndex in 0 until backgroundsJson.length()) {
            val backgroundJson = backgroundsJson.getJSONObject(backgroundIndex)
            val name = backgroundJson.getString("name")
            val desc = backgroundJson.getString("desc")
            val features = extractFeatures(backgroundJson.getJSONArray("features"))
            val languages = mutableListOf<Language>()
            val languageChoices = mutableListOf<LanguageChoice>()
            try {
                val languagesJson = backgroundJson.getJSONArray("languages")
                for (langIndex in 0 until languagesJson.length()) {
                    val langJson = languagesJson.getJSONObject(langIndex)
                    var choose = 0
                    try {
                        choose = langJson.getInt("choose")
                    } catch (e: JSONException) {
                    }

                    if (choose == 0) {
                        languages.add(Language(langJson.getString("name")))
                    } else {
                        val from = mutableListOf<Language>()
                        var index: String? = null
                        var langName: String? = null
                        val languageChoicesJson = langJson.getJSONArray("from")
                        for (langChoiceIndex in 0 until languageChoicesJson.length()) {
                            val choiceJson = languageChoicesJson.getJSONObject(langChoiceIndex)

                            try {
                                index = choiceJson.getString("index")
                                if (index == "all_languages") { //If we add more language indexes this may need to be moved into a function
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
            } catch (e: JSONException) {
            }

            val proficiencies = mutableListOf<Proficiency>()
            val proficiencyChoices = mutableListOf<ProficiencyChoice>()
            try {
                extractProficienciesChoices(
                    backgroundJson.getJSONArray("proficiencies"),
                    proficiencyChoices,
                    proficiencies
                )
            } catch (e: JSONException) {

            }

            val equipmentChoices = mutableListOf<ItemChoice>()
            val equipment = mutableListOf<ItemInterface>()
            val itemChoicesJson = backgroundJson.getJSONArray("equipment")
            extractEquipmentChoices(itemChoicesJson, equipmentChoices, equipment)


            val spells = try {
                val result = mutableListOf<Spell>()
                val backgroundSpellsJson = backgroundJson.getJSONArray("spells")
                for (index in 0 until backgroundSpellsJson.length()) {
                    val spellJson = backgroundSpellsJson.getJSONObject(index)
                    val spell = getSpellsByIndex(spellJson.getString("name"))
                    //TODO once all the spells are added put an exception here if the sell lists is not exactly one item.
                    spell?.getOrNull(0)?.let {
                        result.add(
                            it
                        )
                    }
                }
                result
            } catch (e: JSONException) {
                null
            }

            scope.launch {
                dao.insertBackground(
                    Background(
                        name = name,
                        desc = desc,
                        spells = spells,
                        proficiencies = proficiencies,
                        proficiencyChoices = proficiencyChoices,
                        languages = languages,
                        languageChoices = languageChoices,
                        equipment = equipment,
                        equipmentChoices = equipmentChoices,
                    ).run {
                        this.id = backgroundIndex + 1
                        this
                    }
                )

                features.forEach {
                    dao.insertBackgroundFeatureCrossRef(
                        BackgroundFeatureCrossRef(
                            backgroundId = backgroundIndex + 1,
                            featureId = it
                        )
                    )
                }
            }
        }
    }

    private fun updateRaces() {
        val dataAsString =
            context.resources.openRawResource(R.raw.races).bufferedReader().readText()
        val rootJson = JSONObject(dataAsString)
        val racesJson = rootJson.getJSONArray("races")
        for (raceIndex in 0 until racesJson.length()) {
            val raceJson = racesJson.getJSONObject(raceIndex)
            val name = raceJson.getString("name")
            val groundSpeed = raceJson.getInt("ground_speed")
            val alignment = try {
                raceJson.getString("alignment")
            } catch (e: JSONException) {
                null
            }
            val age = raceJson.getString("age")
            val size = raceJson.getString("size")
            val sizeDesc = raceJson.getString("size_desc")
            val startingProficiencies = mutableListOf<Proficiency>()
            val proficiencyChoices = mutableListOf<ProficiencyChoice>()
            try {
                extractProficienciesChoices(
                    raceJson.getJSONArray("proficiencies"),
                    proficiencyChoices,
                    startingProficiencies
                )
            } catch (e: JSONException) {
            }
            val languageDesc = raceJson.getString("language_desc")
            val traits = extractFeatures(raceJson.getJSONArray("features"))
            val subraces = try {
                extractSubraces(raceJson.getJSONArray("subraces"))
            } catch (e: JSONException) {
                listOf()
            }
            val languages = mutableListOf<Language>()
            val languageChoices = mutableListOf<LanguageChoice>()
            extractLangs(raceJson.getJSONArray("languages"), languages, languageChoices)


            val abilityBonuses = mutableListOf<AbilityBonus>()
            var abilityBonusChoice: AbilityBonusChoice? = null
            try {
                abilityBonusChoice =
                    extractAbilityBonuses(raceJson.getJSONArray("ability_bonuses"), abilityBonuses)
            } catch (e: JSONException) {
            }

            scope.launch {
                dao.insertRace(
                    Race(
                        name = name,
                        groundSpeed = groundSpeed,
                        abilityBonuses = abilityBonuses,
                        abilityBonusChoice = abilityBonusChoice,
                        proficiencyChoices = proficiencyChoices,
                        alignment = alignment,
                        age = age,
                        size = size,
                        sizeDesc = sizeDesc,
                        startingProficiencies = startingProficiencies,
                        languages = languages,
                        languageChoices = languageChoices,
                        languageDesc = languageDesc,
                        subraces = subraces,
                        id = raceIndex + 1
                    )
                )

                traits.forEach {
                    dao.insertRaceFeatureCrossRef(
                        RaceFeatureCrossRef(
                            raceId = raceIndex + 1,
                            featureId = it
                        )
                    )
                }
            }
        }
    }

    private fun extractSubraces(jsonArray: JSONArray): List<Subrace> {
        val subraces = mutableListOf<Subrace>()
        for (index in 0 until jsonArray.length()) {
            val subraceJson = jsonArray.getJSONObject(index)
            val languages = mutableListOf<Language>()
            val languageChoices = mutableListOf<LanguageChoice>()
            try {
                extractLangs(subraceJson.getJSONArray("languages"), languages, languageChoices)
            } catch (e: JSONException) {

            }
            val abilityBonuses = mutableListOf<AbilityBonus>()
            var abilityBonusChoice: AbilityBonusChoice? = null
            try {
                abilityBonusChoice = extractAbilityBonuses(
                    subraceJson.getJSONArray("ability_bonuses"),
                    abilityBonuses
                )
            } catch (e: JSONException) {
            }
            val proficiencies = mutableListOf<Proficiency>()
            val proficiencyChoices = mutableListOf<ProficiencyChoice>()
            try {
                extractProficienciesChoices(
                    subraceJson.getJSONArray("proficiencies"),
                    proficiencyChoices,
                    proficiencies
                )
            } catch (e: JSONException) {
            }

            subraces.add(
                Subrace(
                    name = subraceJson.getString("name"),
                    abilityBonuses = abilityBonuses,
                    abilityBonusChoice = abilityBonusChoice,
                    startingProficiencies = proficiencies,
                    languages = languages,
                    languageChoices = languageChoices,
                    featChoices = try {
                        extractFeatChoices(subraceJson.getJSONArray("feats"))
                    } catch (e: JSONException) {
                        null
                    },
                    /*traits = try {
                        extractFeatures(subraceJson.getJSONArray("features"))
                    } catch (e: JSONException) {
                        listOf()
                    }, TODO */
                    size = try {
                        subraceJson.getString("size")
                    } catch (e: JSONException) {
                        null
                    },
                    groundSpeed = try {
                        subraceJson.getInt("ground_speed")
                    } catch (e: JSONException) {
                        null
                    },
                )
            )
        }
        return subraces
    }

    private fun extractFeatChoices(jsonArray: JSONArray): List<FeatChoice> {
        val result = mutableListOf<FeatChoice>()
        for (index in 0 until jsonArray.length()) {
            val featChoiceJson = jsonArray.getJSONObject(index)
            result.add(
                FeatChoice(
                    name = featChoiceJson.getString("name"),
                    choose = featChoiceJson.getInt("choose"),
                    from = extractFeats(featChoiceJson.getJSONArray("from"))
                )
            )
        }
        return result
    }

    //As far as we have implemented this only needs to work off of indexes.
    //In the future it may be necessary to add more indexes or the ability to create feats
    //from data in the list.
    private fun extractFeats(jsonArray: JSONArray): List<Feat> {
        val result = mutableListOf<Feat>()
        for (i in 0 until jsonArray.length()) {
            val featJson = jsonArray.getJSONObject(i)
            when (featJson.getString("index")) {
                "all_feats" -> {
                    result.addAll(
                        _feats.value!!
                    )
                }
            }
        }
        return result
    }


    private fun extractLangs(
        languagesJson: JSONArray,
        languages: MutableList<Language>,
        languageChoices: MutableList<LanguageChoice>
    ) {
        for (langIndex in 0 until languagesJson.length()) {
            val langJson = languagesJson.getJSONObject(langIndex)
            var choose = 0
            try {
                choose = langJson.getInt("choose")
            } catch (e: JSONException) {
            }

            if (choose == 0) {
                languages.add(Language(langJson.getString("name")))
            } else {
                val from = mutableListOf<Language>()
                var index: String? = null
                var langName: String? = null
                val languageChoicesJson = langJson.getJSONArray("from")
                for (langChoiceIndex in 0 until languageChoicesJson.length()) {
                    val choiceJson = languageChoicesJson.getJSONObject(langChoiceIndex)

                    try {
                        index = choiceJson.getString("index")
                        if (index == "all_languages") { //If we add more language indexes this may need to be moved into a function
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
    }


    private fun extractAbilityBonuses(
        abilityBonusesJson: JSONArray,
        abilityBonuses: MutableList<AbilityBonus>
    ): AbilityBonusChoice? {
        var abilityBonusChoice: AbilityBonusChoice? = null
        for (abilityBonusIndex in 0 until abilityBonusesJson.length()) {
            val abilityBonusJson = abilityBonusesJson.getJSONObject(abilityBonusIndex)
            try {
                when (abilityBonusJson.getString("index")) {
                    "any" -> {
                        abilityBonusChoice = AbilityBonusChoice(
                            from = AbilityBonusChoice.allStatsArray,
                            choose = abilityBonusJson.getInt("total"),
                            maxOccurrencesOfAbility = abilityBonusJson.getInt("max_same")
                        )
                    }
                }
            } catch (e: JSONException) {
                try {
                    val from = mutableListOf<AbilityBonus>()
                    extractAbilityBonuses(abilityBonusJson.getJSONArray("from"), from)
                    abilityBonusChoice = AbilityBonusChoice(
                        choose = abilityBonusJson.getInt("choose"),
                        from = from,
                        maxOccurrencesOfAbility = try {
                            abilityBonusJson.getInt("max_same")
                        } catch (e: JSONException) {
                            1
                        }
                    )
                } catch (e: JSONException) {
                    abilityBonuses.add(
                        AbilityBonus(
                            ability = abilityBonusJson.getString("name"),
                            bonus = abilityBonusJson.getInt("bonus")
                        )
                    )
                }
            }
        }
        return abilityBonusChoice
    }

    private fun extractProficienciesChoices(
        json: JSONArray,
        choices: MutableList<ProficiencyChoice>,
        proficiencies: MutableList<Proficiency>
    ) {
        val addProfsToList = fun(json: JSONObject, list: MutableList<Proficiency>) {
            try {
                list.addAll(getProficienciesByIndex(json.getString("index")))
            } catch (e: JSONException) {
                list.add(
                    Proficiency(
                        name = json.getString("name")
                    )
                )
            }
        }


        for (index in 0 until json.length()) {
            val profJson = json.getJSONObject(index)
            val choose = try {
                profJson.getInt("choose")
            } catch (e: JSONException) {
                0
            }

            if (choose == 0) {
                addProfsToList(profJson, proficiencies)
            } else {
                val from = mutableListOf<Proficiency>()
                val fromJson = profJson.getJSONArray("from")
                for (fromIndex in 0 until fromJson.length()) {
                    addProfsToList(fromJson.getJSONObject(fromIndex), from)
                }
                choices.add(
                    ProficiencyChoice(
                        name = profJson.getString("name"),
                        choose = choose,
                        from = from
                    )
                )
            }
        }
    }

    private fun getProficienciesByIndex(index: String): List<Proficiency> {
        val proficiencies = mutableListOf<Proficiency>()
        when (index) {
            "gaming_sets" -> {
                gamingSetIndexes.forEach {
                    proficiencies.addAll(getProficienciesByIndex(it))
                }
            }
            "artisans_tools" -> {
                artisansToolIndexes.forEach {
                    proficiencies.add(
                        Proficiency(
                            name = it
                        )
                    )
                }
            }
            "skill_proficiencies" -> {
                _abilitiesToSkills.value!!.values.forEach {
                    it.forEach { item ->
                        if (!item.contains("Saving")) {
                            proficiencies.add(
                                Proficiency(
                                    name = item
                                )
                            )
                        }
                    }
                }
            }
            "musical_instruments" -> {
                instrumentIndexes.forEach {
                    proficiencies.add(
                        Proficiency(
                            name = it
                        )
                    )
                }
            }
            else -> {
                throw JSONException("Unknown index: $index")
            }
        }
        return proficiencies
    }


    private fun updateClasses() {
        val dataAsString =
            context.resources.openRawResource(R.raw.classes).bufferedReader().readText()

        val rootJson = JSONObject(dataAsString)
        val classesJson = rootJson.getJSONArray("classes")
        for (classIndex in 0 until classesJson.length()) {
            val classJson = classesJson.getJSONObject(classIndex)
            val name = classJson.getString("name")
            val hitDie = classJson.getInt("hit_die")
            val featureIds = extractFeatures(classJson.getJSONArray("features"))
            val proficiencyChoices = mutableListOf<ProficiencyChoice>()
            val proficiencies = mutableListOf<Proficiency>()
            val equipmentChoices: MutableList<ItemChoice> = mutableListOf()
            val equipment: MutableList<ItemInterface> = mutableListOf()
            extractEquipmentChoices(
                classJson.getJSONArray("equipment"),
                equipmentChoices,
                equipment
            )
            try {
                extractProficienciesChoices(
                    classJson.getJSONArray("proficiencies"),
                    proficiencyChoices,
                    proficiencies
                )
            } catch (e: JSONException) {
            }

            val subclassesJson = classJson.getJSONArray("subclasses")
            for (subclassIndex in 0 until subclassesJson.length()) {
                val subclassJson = subclassesJson.getJSONObject(subclassIndex)
                val spellsAreFree = try {
                    subclassJson.getBoolean("spells_are_free")
                } catch (e: JSONException) {
                    false
                }

                scope.launch {
                    val id = subclassJson.getInt("id")
                    dao.insertSubclass(
                        SubclassEntity(
                            name = subclassJson.getString("name"),
                            spellCasting = try {
                                extractSpellCasting(subclassJson.getJSONObject("spell_casting"))
                            } catch (e: JSONException) {
                                null
                            },
                            spellAreFree = spellsAreFree
                        ).run {
                            this.subclassId = id
                            this
                        }
                    )

                    dao.insertClassSubclassId(
                        ClassSubclassCrossRef(
                            classId = classIndex + 1,
                            subclassId = id
                        )
                    )

                    extractFeatures(subclassJson.getJSONArray("features")).forEach {
                        dao.insertSubclassFeatureCrossRef(
                            SubclassFeatureCrossRef(
                                featureId = it,
                                subclassId = id
                            )
                        )
                    }

                   try {
                        val subclassSpellJson = subclassJson.getJSONArray("spells")
                        for (index in 0 until subclassSpellJson.length()) {
                            val spellJson = subclassSpellJson.getJSONObject(index)
                            val spellId = dao.getSpellIdByName(spellJson.getString("name"))
                            dao.insertSubclassSpellCrossRef(
                                SubclassSpellCrossRef(
                                    subclassId = id,
                                    spellId = spellId
                                )
                            )
                        }

                    } catch (_: JSONException) { }


                }
            }

            val spellCastingJson = classJson.getJSONObject("spell_casting")
            val spellCasting = extractSpellCasting(spellCastingJson)

            val pactMagic = try {
                val pactMagicJson = classJson.getJSONObject("pact_magic")
                val spellsKnownJson = pactMagicJson.getJSONArray("spells_known")
                val spellsKnown = mutableListOf<Int>()
                for (i in 0 until spellsKnownJson.length()) {
                    spellsKnown.add(spellsKnownJson.getInt(i))
                }

                val cantripsKnownJson = pactMagicJson.getJSONArray("cantrips_known")
                val cantripsKnown = mutableListOf<Int>()
                for (i in 0 until cantripsKnownJson.length()) {
                    cantripsKnown.add(cantripsKnownJson.getInt(i))
                }

                val pactSlotsJson = pactMagicJson.getJSONArray("pact_slots")
                val pactSlots: MutableList<Resource> = mutableListOf()
                for (i in 0 until pactSlotsJson.length()) {
                    val pactSlotJson = pactSlotsJson.getJSONObject(i)
                    val amount = pactSlotJson.getInt("amount")
                    pactSlots.add(
                        Resource(
                            name = pactSlotJson.getInt("level").toString(),
                            rechargeAmountType = amount.toString(),
                            currentAmount = amount,
                            maxAmountType = amount.toString()
                        )
                    )
                }

                PactMagic(
                    spellsKnown = spellsKnown,
                    castingAbility = pactMagicJson.getString("casting_ability"),
                    cantripsKnown = cantripsKnown,
                    pactSlots = pactSlots
                )
            } catch (e: JSONException) {
                null
            }

            scope.launch {
                dao.insertClass(
                    Class(
                        name = name,
                        hitDie = hitDie,
                        levelPath = null,
                        proficiencyChoices = proficiencyChoices,
                        proficiencies = proficiencies,
                        equipmentChoices = equipmentChoices,
                        equipment = equipment,
                        spellCasting = spellCasting,
                        pactMagic = pactMagic,
                        subclassLevel = classJson.getInt("subclass_level"),
                        startingGoldD4s = classJson.getInt("starting_gold_d4s"),
                        startingGoldMultiplier = try {
                            classJson.getInt("staring_gold_multiplier")
                        } catch (e: JSONException) {
                            10
                        },
                        id = classIndex + 1
                    )
                )

                featureIds.forEach {
                    dao.insertClassFeatureCrossRef(
                        ClassFeatureCrossRef(
                            id = classIndex + 1,
                            featureId = it
                        )
                    )
                }
            }
        }
    }

    private fun extractSpellCasting(spellCastingJson: JSONObject): SpellCasting? {
        val spellCastingType = spellCastingJson.getDouble("type")
        return if (spellCastingType == 0.0) {
            null
        } else {
            val learnFrom = try {
                val learnFromJson = spellCastingJson.getJSONObject("learn_spells_from")
                val listsJson = learnFromJson.getJSONArray("lists")
                val result = mutableListOf<String>()
                for (index in 0 until listsJson.length()) {
                    result.add(
                        listsJson.getString(index)
                    )
                }
                result
            } catch (e: JSONException) {
                null
            }
            val schoolRestriction = try {
                val restrictionJson = spellCastingJson.getJSONObject("school_restriction")
                SchoolRestriction(
                    schools = restrictionJson.getJSONArray("schools").let {
                        val result = mutableListOf<String>()
                        for (index in 0 until it.length()) {
                            result.add(it.getString(index))
                        }
                        result
                    },
                    amount = restrictionJson.getInt("minimum")
                )
            } catch (e: JSONException) {
                null
            }
            SpellCasting(
                type = spellCastingType,
                hasSpellBook = try {
                    spellCastingJson.getBoolean("has_spell_book")
                } catch (e: JSONException) {
                    false
                },
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
                    for (i in 0 until spellsKnownJson.length()) {
                        result.add(spellsKnownJson.getInt(i))
                    }
                    result
                } catch (e: JSONException) {
                    null
                },
                cantripsKnown = try {
                    val spellsKnownJson = spellCastingJson.getJSONArray("cantrips_known")
                    val result = mutableListOf<Int>()
                    for (i in 0 until spellsKnownJson.length()) {
                        result.add(spellsKnownJson.getInt(i))
                    }
                    result
                } catch (e: JSONException) {
                    null
                },
                spellSlotsByLevel = spellCastingJson.getJSONArray("spell_slots").let {
                    val result = mutableListOf<List<Resource>>()
                    for (levelIndex in 0 until it.length()) {
                        val level = mutableListOf<Resource>()
                        val levelJson = it.getJSONArray(levelIndex)
                        for (slotIndex in 0 until levelJson.length()) {
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
                },
                learnFrom = learnFrom,
                schoolRestriction = schoolRestriction
            )
        }
    }

    private fun extractEquipmentChoices(
        jsonArray: JSONArray,
        itemChoices: MutableList<ItemChoice>,
        items: MutableList<ItemInterface>
    ) {
        for (i in 0 until jsonArray.length()) {
            val json = jsonArray.getJSONObject(i)
            var choose = 0
            try {
                choose = json.getInt("choose")
            } catch (e: JSONException) {
            }

            if (choose == 0) {
                try {
                    when (val index = json.getString("index")) {
                        "gold" -> {
                            items.add(
                                Currency(
                                    backingDisplayName = json.getString("name"),
                                    name = "Gold pieces",
                                    amount = json.getInt("amount")
                                )
                            )
                        }
                        "silver" -> {
                            items.add(
                                Currency(
                                    backingDisplayName = json.getString("name"),
                                    name = "Silver pieces",
                                    amount = json.getInt("amount")
                                )
                            )
                        }
                        else -> {
                            getItemByIndex(index)?.let { items.add(it) }
                        }
                    }
                } catch (e: JSONException) {
                    items.add(
                        Item(
                            name = json.getString("name")
                        )
                    )
                }
            } else {
                val from = mutableListOf<List<ItemInterface>>()
                val fromJson = json.getJSONArray("from")
                //Get all the options the user can choose from.
                for (fromIndex in 0 until fromJson.length()) {
                    try {
                        val itemJson = fromJson.getJSONObject(fromIndex)
                        try {
                            //Get by index
                            getItemsByIndex(
                                itemJson.getString("index")
                            )?.let {
                                it.forEach { item ->
                                    from.add(listOf(item))
                                }
                            }
                        } catch (e: JSONException) {
                            //Construct
                            from.add(
                                listOf(
                                    Item(
                                        name = itemJson.getString("name")
                                    )
                                )
                            )
                        }
                    } catch (e: JSONException) {
                        val itemsJson = fromJson.getJSONArray(fromIndex)
                        val sublist = mutableListOf<ItemInterface>()
                        for (index in 0 until itemsJson.length()) {
                            val itemJson = itemsJson.getJSONObject(index)
                            try {
                                //Get by index
                                getItemByIndex(
                                    itemJson.getString("index")
                                )?.let { sublist.add(it) }
                            } catch (e: JSONException) {
                                //Construct
                                sublist.add(
                                    Item(name = itemJson.getString("name"))
                                )
                            }
                        }
                        from.add(sublist)
                    }
                }

                val listsCostOne = try {
                    json.getBoolean("lists_cost_one")
                } catch (_: JSONException) {
                    true
                }

                val maxSame = try {
                    json.getInt("max_same")
                } catch (_: JSONException) {
                    1
                }

                itemChoices.add(
                    ItemChoice(
                        name = json.getString("name"),
                        choose = choose,
                        from = from,
                        listsCostOne = listsCostOne,
                        maxSame = maxSame
                    )
                )
            }
        }
    }

    private fun extractFeatures(featuresJson: JSONArray): List<Int> {
        val ids = mutableListOf<Int>()
        try {
            for (featureIndex in 0 until featuresJson.length()) {
                val featureJson = featuresJson.getJSONObject(featureIndex)
                val maxActive = try {
                    Choose(featureJson.getInt("max_active"))
                } catch (e: JSONException) {
                    try {
                        val result = mutableListOf<Int>()
                        val json = featureJson.getJSONArray("max_active")
                        for (i in 0 until json.length()) {
                            result.add(json.getInt(i))
                        }
                        Choose(result)
                    } catch (e: JSONException) {
                        Choose(0)
                    }
                }
                val featureId = featureJson.getInt("id")
                val level = try {
                    featureJson.getInt("level")
                } catch (e: JSONException) {
                    0
                }

                //If we have an index construct a list from that otherwise just make it normally.
                try {
                    when (val index = featureJson.getString("index")) {
                        "invocations" -> {
                            _invocations.value!!.forEach {

                                Feature(
                                    name = it.name,
                                    prerequisite = it.prerequisite,
                                    description = it.desc,
                                )

                            }
                        }
                        "infusions" -> {
                            _infusions.value!!.forEach {
                                val choices = if (it.options != null && it.choose != 0) {
                                    listOf(
                                        FeatureChoice(
                                            options = it.options as MutableList<Feature>?,
                                            choose = Choose(static = it.choose)
                                        )
                                    )
                                } else {
                                    null
                                }


                                Feature(
                                    maxTimesChosen = it.maxTimesChosen,
                                    grantedAtLevel = it.grantedAtLevel,
                                    name = it.name,
                                    description = it.desc,
                                    infusion = it,
                                    choices = choices,
                                    featureId = it.id
                                )

                            }
                        }
                        "proficiencies" -> {
                            _abilitiesToSkills.value!!.forEach {
                                it.value.forEach { skill ->
                                    //Don't add saving throws
                                    if (!skill.lowercase().contains("throw")) {

                                        Feature(
                                            name = skill,
                                            description = "",
                                            grantedAtLevel = 0,
                                            prerequisite = Prerequisite(
                                                proficiency = Proficiency(
                                                    name = skill
                                                )
                                            )
                                        )
                                    }
                                }
                            }
                        }
                        "skills" -> {
                            _abilitiesToSkills.value!!.values.forEach {
                                it.forEach { item ->
                                    if (!item.contains("Saving")) {

                                        Feature(
                                            name = item,
                                            proficiencies = listOf(Proficiency(name = item)),
                                            description = ""
                                        )

                                    }
                                }
                            }
                        }
                        "all_spells" -> {
                            val bound = try {
                                featureJson.getInt("bound")
                            } catch (e: JSONException) {
                                null
                            }
                            /*_spells.value?.forEach { spell ->
                                if (spell.level <= bound ?: 10) {

                                    Feature(
                                        name = spell.name,
                                        spells = listOf(spell),
                                        description = "",
                                    )

                                }
                            } TODO */
                        }
                        "maneuvers" -> {

                            _maneuvers.value!!

                        }
                        "metamagic" -> {
                            _metaMagics.value?.forEach {

                                Feature(
                                    name = it.name,
                                    description = it.desc
                                )

                            }
                        }
                        "all_languages" -> {
                            _languages.value!!.forEach {

                                Feature(
                                    name = it.name!!,
                                    languages = listOf(it),
                                    description = "You can speak read and write ${it.name}."
                                )

                            }
                        }
                        "artisans_tools" -> {
                            artisansToolIndexes.forEach {

                                Feature(
                                    name = it,
                                    proficiencies = listOf(Proficiency(it)),
                                    description = ""
                                )

                            }
                        }
                        "spells" -> {
                            val levels = try {
                                featureJson.getJSONArray("levels").let {
                                    val result = mutableListOf<Int>()
                                    for (i in 0 until it.length()) {
                                        result.add(it.getInt(i))
                                    }
                                    result
                                }
                            } catch (e: JSONException) {
                                null
                            }

                            val classes = try {
                                featureJson.getJSONArray("classes").let {
                                    val result = mutableListOf<String>()
                                    for (i in 0 until it.length()) {
                                        result.add(it.getString(i))
                                    }
                                    result
                                }
                            } catch (e: JSONException) {
                                null
                            }


                            val schools = try {
                                featureJson.getJSONArray("schools").let {
                                    val result = mutableListOf<String>()
                                    for (i in 0 until it.length()) {
                                        result.add(it.getString(i))
                                    }
                                    result
                                }
                            } catch (e: JSONException) {
                                null
                            }

                            val passes = fun(spell: Spell): Boolean {
                                levels?.let {
                                    if (!it.contains(spell.level)) {
                                        return false
                                    }
                                }

                                if (classes != null) {
                                    classes.forEach {
                                        if (spell.classes.contains(it)) {
                                            return true
                                        }
                                    }
                                    return false
                                }

                                if (schools != null) {
                                    schools.forEach {
                                        if (spell.school == it) {
                                            return true
                                        }
                                    }
                                    return false
                                }

                                return true
                            }

                            /*_spells.value!!.filter {
                                passes(it)
                            }.forEach { spell ->

                                Feature(
                                    name = spell.name,
                                    spells = listOf(spell),
                                    description = "",
                                )

                            } TODO */
                        }
                        in _fightingStyles.value.let { styles ->
                            val result = mutableListOf<String>()
                            styles?.forEach {
                                result.add(it.name)
                            }
                            result
                        } -> {

                            _fightingStyles.value!!.single {
                                it.name == index
                            }

                        }
                        else -> throw JSONException("Invalid index")
                    }

                } catch (e: JSONException) {

                    val extractAndAddChoice = fun(choiceJson: JSONObject) {
                        scope.launch {
                            val optionsIds = try {
                                extractFeatures(choiceJson.getJSONArray("from"))
                            } catch (e: JSONException) {
                                null
                            }

                            val choose = try {
                                Choose(choiceJson.getInt("choose"))
                            } catch (e: JSONException) {
                                try {
                                    val result = mutableListOf<Int>()
                                    val json = choiceJson.getJSONArray("choose")
                                    for (i in 0 until json.length()) {
                                        result.add(json.getInt(i))
                                    }
                                    Choose(result)
                                } catch (e: JSONException) {
                                    Choose(0)
                                }
                            }
                            if (optionsIds?.isNotEmpty() == true && choose != Choose(0)) {
                                val choiceId = choiceJson.getInt("choice_id")
                                val entity = FeatureChoiceEntity(
                                    choose
                                ).run {
                                    this.id = choiceId
                                    this
                                }

                                val optionsRef = FeatureOptionsCrossRef(
                                    featureId = featureId,
                                    id = choiceId
                                )

                                dao.insertFeatureChoice(
                                    entity
                                )

                                dao.insertFeatureOptionsCrossRef(
                                    optionsRef
                                )


                                optionsIds.forEach {
                                    val ref = OptionsFeatureCrossRef(
                                        featureId = it,
                                        choiceId = choiceId
                                    )

                                    dao.insertOptionsFeatureCrossRef(
                                        ref
                                    )

                                }
                            }
                        }
                    }

                    //Look for an array called choices
                    //If you cant find one try to make a feature choice by looking for a choose
                    //And a from
                    try {
                        val choicesJson = featureJson.getJSONArray("choices")
                        for (choiceIndex in 0 until choicesJson.length()) {
                            extractAndAddChoice(choicesJson.getJSONObject(choiceIndex))
                        }
                    } catch (e: JSONException) {
                        extractAndAddChoice(featureJson)
                    }


                    val hpBonusPerLevel = try {
                        featureJson.getInt("hp_bonus_per_level")
                    } catch (e: JSONException) {
                        null
                    }

                    //This is for spells granted by the feature.
                    //Not for choosing what spells you get.
                    //For that create sub features with the spells you
                    //Need the user to choose from.
                    val spells = try {
                        extractSpells(featureJson.getJSONArray("spells"))
                    } catch (e: JSONException) {
                        null
                    }

                    val ac = try {
                        val acJson = featureJson.getJSONObject("ac")
                        ArmorClass(
                            base = acJson.getInt("base"),
                            dexMax = acJson.getInt("dex"),
                            conMax = acJson.getInt("con"),
                            wisMax = acJson.getInt("wis")
                        )
                    } catch (e: JSONException) {
                        null
                    }

                    val languages = try {
                        val languagesJson = featureJson.getJSONArray("languages")
                        val result = mutableListOf<Language>()
                        for (i in 0 until languagesJson.length()) {
                            result.add(
                                Language(
                                    name = languagesJson.getJSONObject(i).getString("name")
                                )
                            )
                        }
                        result
                    } catch (e: JSONException) {
                        null
                    }


                    val proficiencies = try {
                        val proficienciesJson = featureJson.getJSONArray("proficiencies")
                        val result = mutableListOf<Proficiency>()
                        for (i in 0 until proficienciesJson.length()) {
                            result.add(
                                Proficiency(
                                    name = proficienciesJson.getJSONObject(i).getString("name")
                                )
                            )
                        }
                        result
                    } catch (e: JSONException) {
                        null
                    }

                    val armorContingentAcBonus = try {
                        featureJson.getInt("armor_contingent_ac_bonus")
                    } catch (e: JSONException) {
                        null
                    }
                    val extraAttackAndDamageRollStat = try {
                        featureJson.getString("extra_attack_and_damage_roll_stat")
                    } catch (e: JSONException) {
                        null
                    }
                    val rangedAttackBonus = try {
                        featureJson.getInt("ranged_attack_bonus")
                    } catch (e: JSONException) {
                        null
                    }

                    val activationRequirement =
                        featureJson.optJSONObject("activation_requirement").let {
                            if (it == null) {
                                ActivationRequirement()
                            } else {
                                extractActivationRequirement(it)
                            }
                        }

                    val speedBoost = featureJson.optJSONArray("speed_boost").let {
                        if (it == null) {
                            null
                        } else {
                            extractScalingBonus(it)
                        }
                    }

                    val expertises = featureJson.optJSONArray("expertises").let {
                        if (it == null) {
                            null
                        } else {
                            val result = mutableListOf<Proficiency>()
                            for (i in 0 until it.length()) {
                                result.add(Proficiency(name = it.getString(i)))
                            }
                            result
                        }
                    }

                    val feature = Feature(
                        name = featureJson.getString("name"),
                        activationRequirement = activationRequirement,
                        rangedAttackBonus = rangedAttackBonus,
                        extraAttackAndDamageRollStat = extraAttackAndDamageRollStat,
                        maxTimesChosen = try {
                            featureJson.getInt("max_times_chosen")
                        } catch (e: JSONException) {
                            null
                        },
                        description = try {
                            featureJson.getString("desc")
                        } catch (e: JSONException) {
                            ""
                        },
                        grantedAtLevel = level,
                        hpBonusPerLevel = hpBonusPerLevel,
                        maxActive = maxActive,
                        index = try {
                            featureJson.getString("index")
                        } catch (e: JSONException) {
                            null
                        },
                        spells = spells,
                        acBonus = try {
                            featureJson.getInt("ac_bonus")
                        } catch (e: JSONException) {
                            null
                        },
                        ac = ac,
                        armorContingentAcBonus = armorContingentAcBonus,
                        proficiencies = proficiencies,
                        languages = languages,
                        speedBoost = speedBoost,
                        expertises = expertises,
                        featureId = featureId
                    )
                    ids.add(featureId)
                    scope.launch {
                        dao.insertFeature(feature)
                    }
                }
            }
        } catch (e: JSONException) {
            throw e
        }
        return ids
    }

    private fun extractSpells(spellsJson: JSONArray): List<Spell> {
        val spells = mutableListOf<Spell>()
        for (spellIndex in 0 until spellsJson.length()) {
            val spellJson = spellsJson.getJSONObject(spellIndex)
            getSpellsByIndex(spellJson.getString("index"))?.let { spells.addAll(it) }
        }
        return spells
    }

    private fun extractActivationRequirement(json: JSONObject): ActivationRequirement {
        return ActivationRequirement(
            armorReqIndex = json.optString("armor_index"),
            shieldReq = json.optBoolean("shield")
        )
    }
}
