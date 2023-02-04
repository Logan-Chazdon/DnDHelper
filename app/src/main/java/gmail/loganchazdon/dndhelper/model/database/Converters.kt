package gmail.loganchazdon.dndhelper.model.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import gmail.loganchazdon.dndhelper.model.*
import java.lang.reflect.Type

class Converters {
    var gson = Gson()
    init {
        val converter = ItemConverter("type")
        converter.registerItemType("Item", Item::class.java)
        converter.registerItemType("Weapon", Weapon::class.java)
        converter.registerItemType("Armor", Armor::class.java)
        converter.registerItemType("Currency", Currency::class.java)
        converter.registerItemType("Shield", Shield::class.java)
        gson = GsonBuilder()
            .registerTypeAdapter(ItemInterface::class.java, converter)
            .create()

    }


    @TypeConverter
    fun storedStringToListListString(data: String?): List<List<String>> {
        if (data == null || data.isBlank()) {
            return mutableListOf()
        }
        val listType: Type = object : TypeToken<List<List<String>>>() {}.type
        return gson.fromJson(data, listType)
    }


    @TypeConverter
    fun listListStringToStoredString(myObjects: List<List<String>>): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun stringToMutableListOfMapOfStringToInt(data: String?): MutableList<Map<String, Int>> {
        if (data == null || data.isBlank()) {
            return mutableListOf()
        }
        val listType: Type = object : TypeToken<MutableList<Map<String, Int>>>() {}.type
        return gson.fromJson(data, listType)
    }


    @TypeConverter
    fun mutableListOfMapOfStringToIntToStoredString(myObjects: MutableList<Map<String, Int>>): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToClasses(data: String?): MutableMap<String, Class> {
        if (data == null || data.isBlank()) {
            return mutableMapOf()
        }
        val listType: Type = object : TypeToken<MutableMap<String, Class>>() {}.getType()
        return gson.fromJson(data, listType)
    }


    @TypeConverter
    fun classToStoredString(myObjects: MutableMap<String, Class>): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToBackpack(data: String?): Backpack {
        val listType: Type = object : TypeToken<Backpack>() {}.getType()
        return gson.fromJson(data, listType)
    }


    @TypeConverter
    fun backpackToStoredString(myObjects: Backpack): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToSubClass(data: String?): Subclass? {
        if (data == null) {
            return null
        }
        val listType: Type = object : TypeToken<Subclass?>() {}.getType()
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun subClassToStoredString(myObjects: Subclass?): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToCasting(data: String?): SpellCasting? {
        if (data == null) {
            return null
        }
        val listType: Type = object : TypeToken<SpellCasting?>() {}.getType()
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun castingToStoredString(myObjects: SpellCasting?): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToPactMagic(data: String?): PactMagic? {
        if (data == null) {
            return null
        }
        val listType: Type = object : TypeToken<PactMagic?>() {}.getType()
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun pactMagicToStoredString(myObjects: PactMagic?): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToSubClassList(data: String?): MutableList<Subclass> {
        if (data == null) {
            return mutableListOf()
        }
        val listType: Type = object : TypeToken<MutableList<Subclass>>() {}.getType()
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun subClassListToStoredString(myObjects: MutableList<Subclass>): String? {
        return gson.toJson(myObjects)
    }



    @TypeConverter
    fun storedStringToFeatureList(data: String?): MutableList<Feature>? {
        if (data == null) {
            return mutableListOf()
        }
        val listType: Type = object : TypeToken<MutableList<Feature>?>() {}.getType()
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun featureListToStoredString(myObjects: MutableList<Feature>?): String? {
        return gson.toJson(myObjects)
    }


    //Races
    @TypeConverter
    fun storedStringToAbilityBonus(data: String?): List<AbilityBonus>? {
        if (data == null) {
            return listOf()
        }
        val listType: Type = object : TypeToken<List<AbilityBonus>>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun abilityBonusToStoredString(myObjects: List<AbilityBonus>?): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToAbilityBonusChoice(data: String?): AbilityBonusChoice? {
        if (data == null) {
            return null
        }
        val listType: Type = object : TypeToken<AbilityBonusChoice?>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun abilityBonusChoiceToStoredString(myObjects: AbilityBonusChoice?): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToProficiency(data: String?): List<Proficiency> {
        if (data == null || data == "null") {
            return emptyList()
        }
        val listType: Type = object : TypeToken<List<Proficiency>>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun proficiencyToStoredString(myObjects: List<Proficiency>?): String {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToLanguage(data: String?): List<Language>? {
        if (data == null) {
            return null
        }
        val listType: Type = object : TypeToken<List<Language>?>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun languageToStoredString(myObjects: List<Language>?): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToLanguageChoices(data: String?): List<LanguageChoice>? {
        if (data == null) {
            return null
        }
        val listType: Type = object : TypeToken<List<LanguageChoice>?>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun languageChoicesToStoredString(myObjects: List<LanguageChoice>?): String? {
        return gson.toJson(myObjects)
    }


    @TypeConverter
    fun storedStringToSubraces(data: String?): List<Subrace>? {
        if (data == null) {
            return null
        }
        val listType: Type = object : TypeToken<List<Subrace>?>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun subracesToStoredString(myObjects: List<Subrace>?): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToSubrace(data: String?): Subrace? {
        if (data == null) {
            return null
        }
        val listType: Type = object : TypeToken<Subrace?>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun subraceToStoredString(myObjects: Subrace?): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToStringList(data: String?): List<String>? {
        if (data == null) {
            return null
        }
        val listType: Type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun stringListToStoredString(myObjects: List<String>?): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToRace(data: String?): Race? {
        if (data == null) {
            return null
        }
        val listType: Type = object : TypeToken<Race?>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun raceToStoredString(myObjects: Race?): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToResourceList(data: String?): List<Resource>? {
        if(data == null) {
            return null
        }
        val listType: Type = object : TypeToken<List<Resource>?>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun resourceListToStoredString(myObjects: List<Resource>?): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToStringIntMap(data: String?): MutableMap<String, Int> {
        if (data == null) {
            return mutableMapOf()
        }
        val listType: Type = object : TypeToken<MutableMap<String, Int>?>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun stringIntMapToStoredString(myObjects: MutableMap<String, Int>?): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToBackground(data: String?): Background? {
        if (data == null) {
            return null
        }
        val listType: Type = object : TypeToken<Background?>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun backgroundToStoredString(myObjects: Background?): String? {
        return gson.toJson(myObjects)
    }


    @TypeConverter
    fun storedStringToListOfItem(data: String?): List<Item> {
        if (data == null) {
            return emptyList()
        }
        val listType: Type = object : TypeToken<List<Item>>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun listOfItemToStoredString(myObjects: List<Item>): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToMapOfCurrency(data: String?): Map<String, Currency> {
        if (data == null) {
            return emptyMap()
        }
        val listType: Type = object : TypeToken<Map<String, Currency>>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun mapOfCurrencyToStoredString(myObjects: Map<String, Currency>): String? {
        return gson.toJson(myObjects)
    }




    @TypeConverter
    fun storedStringToListProficiencyChoices(data: String?): List<ProficiencyChoice?>? {
        if (data == null) {
            return null
        }
        val listType: Type = object : TypeToken<List<ProficiencyChoice?>?>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun listOfProficiencyChoicesToStoredString(myObjects: List<ProficiencyChoice?>?): String? {
        return gson.toJson(myObjects)
    }


    @TypeConverter
    fun storedStringToItemChoicesList(data: String?): List<ItemChoice> {
        if (data == null) {
            return emptyList()
        }
        val listType: Type = object : TypeToken<List<ItemChoice>>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun itemChoicesListChoicesToStoredString(myObjects: List<ItemChoice>): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToArmor(data: String?): Armor? {
        if (data == null) {
            return null
        }
        val listType: Type = object : TypeToken<Armor>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun armorToStoredString(myObjects: Armor): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToShield(data: String?): Shield? {
        if (data == null) {
            return null
        }
        val listType: Type = object : TypeToken<Shield?>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun shieldToStoredString(myObjects: Shield?): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToItems(data: String?): List<ItemInterface>? {
        return gson.fromJson(data, object : TypeToken<List<ItemInterface>?>() {}.type)
    }

    @TypeConverter
    fun itemsToStoredString(myObjects: List<ItemInterface>?): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToFeats(data: String?): List<Feat> {
        return gson.fromJson(data, object : TypeToken<List<Feat>>() {}.type)
    }

    @TypeConverter
    fun featsToStoredString(myObjects: List<Feat>): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToPrerequisite(data: String?): Prerequisite? {
        return gson.fromJson(data, object : TypeToken<Prerequisite?>() {}.type)
    }

    @TypeConverter
    fun prerequisiteToStoredString(myObjects: Prerequisite?): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToActivationRequirement(data: String?): ActivationRequirement? {
        return gson.fromJson(data, object : TypeToken<ActivationRequirement?>() {}.type)
    }

    @TypeConverter
    fun activationRequirementToStoredString(myObjects: ActivationRequirement?): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToScalingBonus(data: String?): ScalingBonus? {
        return gson.fromJson(data, object : TypeToken<ScalingBonus?>() {}.type)
    }

    @TypeConverter
    fun scalingBonusToStoredString(myObjects: ScalingBonus?): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToSpells(data: String?): List<Spell>?  {
        return gson.fromJson(data, object : TypeToken<List<Spell>?>() {}.type)
    }

    @TypeConverter
    fun spellsToStoredString(myObjects: List<Spell>?): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToInfusion(data: String?): Infusion? {
        return gson.fromJson(data, object : TypeToken<Infusion?>() {}.type)
    }

    @TypeConverter
    fun infusionToStoredString(myObjects: Infusion?): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToChoose(data: String?): Choose? {
        return gson.fromJson(data, object : TypeToken<Choose?>() {}.type)
    }

    @TypeConverter
    fun chooseToStoredString(myObjects: Choose?): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToArmorClass(data: String?): ArmorClass? {
        return gson.fromJson(data, object : TypeToken<ArmorClass?>() {}.type)
    }

    @TypeConverter
    fun armorClassToStoredString(myObjects: ArmorClass?): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToFeatureChoices(data: String?): List<FeatureChoice>? {
        return gson.fromJson(data, object : TypeToken<List<FeatureChoice>?>() {}.type)
    }

    @TypeConverter
    fun featureChoicesToStoredString(myObjects: List<FeatureChoice>?): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToResource(data: String?): Resource? {
        return gson.fromJson(data, object : TypeToken<Resource?>() {}.type)
    }

    @TypeConverter
    fun resourceChoicesToStoredString(myObjects: Resource?): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToMutableIntList(data: String?): MutableList<Int> {
        return gson.fromJson(data, object : TypeToken<MutableList<Int>>() {}.type)
    }

    @TypeConverter
    fun mutableIntListToStoredString(myObjects: MutableList<Int>?): String? {
        return gson.toJson(myObjects)
    }


    @TypeConverter
    fun storedStringToFeatChoiceList(data: String?): MutableList<FeatChoice> {
        return gson.fromJson(data, object : TypeToken<MutableList<FeatChoice>>() {}.type)
    }

    @TypeConverter
    fun featChoiceListToStoredString(myObjects: MutableList<FeatChoice>): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToListPairIntSpell(data: String?): List<Pair<Int, Spell>>? {
        return gson.fromJson(data, object : TypeToken<List<Pair<Int, Spell>>?>() {}.type)
    }

    @TypeConverter
    fun listPairIntSpellToStoredString(myObjects: List<Pair<Int, Spell>>?): String? {
        return gson.toJson(myObjects)
    }
}