package model.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class Converters {
    var gson = Gson()
    init {
        val converter = ItemConverter("type")
        converter.registerItemType("Item", model.Item::class.java)
        converter.registerItemType("Weapon", model.Weapon::class.java)
        converter.registerItemType("Armor", model.Armor::class.java)
        converter.registerItemType("Currency", model.Currency::class.java)
        converter.registerItemType("Shield", model.Shield::class.java)
        gson = GsonBuilder()
            .registerTypeAdapter(model.ItemInterface::class.java, converter)
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
    fun storedStringToClasses(data: String?): MutableMap<String, model.Class> {
        if (data == null || data.isBlank()) {
            return mutableMapOf()
        }
        val listType: Type = object : TypeToken<MutableMap<String, model.Class>>() {}.getType()
        return gson.fromJson(data, listType)
    }


    @TypeConverter
    fun classToStoredString(myObjects: MutableMap<String, model.Class>): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToBackpack(data: String?): model.Backpack {
        val listType: Type = object : TypeToken<model.Backpack>() {}.getType()
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun backpackToStoredString(myObjects: model.Backpack): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToSubClass(data: String?): model.Subclass? {
        if (data == null) {
            return null
        }
        val listType: Type = object : TypeToken<model.Subclass?>() {}.getType()
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun subClassToStoredString(myObjects: model.Subclass?): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToCasting(data: String?): model.SpellCasting? {
        if (data == null) {
            return null
        }
        val listType: Type = object : TypeToken<model.SpellCasting?>() {}.getType()
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun castingToStoredString(myObjects: model.SpellCasting?): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToPactMagic(data: String?): model.PactMagic? {
        if (data == null) {
            return null
        }
        val listType: Type = object : TypeToken<model.PactMagic?>() {}.getType()
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun pactMagicToStoredString(myObjects: model.PactMagic?): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToSubClassList(data: String?): MutableList<model.Subclass> {
        if (data == null) {
            return mutableListOf()
        }
        val listType: Type = object : TypeToken<MutableList<model.Subclass>>() {}.getType()
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun subClassListToStoredString(myObjects: MutableList<model.Subclass>): String? {
        return gson.toJson(myObjects)
    }



    @TypeConverter
    fun storedStringToFeatureList(data: String?): MutableList<model.Feature>? {
        if (data == null) {
            return mutableListOf()
        }
        val listType: Type = object : TypeToken<MutableList<model.Feature>?>() {}.getType()
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun featureListToStoredString(myObjects: MutableList<model.Feature>?): String? {
        return gson.toJson(myObjects)
    }


    //Races
    @TypeConverter
    fun storedStringToAbilityBonus(data: String?): List<model.AbilityBonus>? {
        if (data == null) {
            return listOf()
        }
        val listType: Type = object : TypeToken<List<model.AbilityBonus>>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun abilityBonusToStoredString(myObjects: List<model.AbilityBonus>?): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToAbilityBonusChoice(data: String?): model.AbilityBonusChoice? {
        if (data == null) {
            return null
        }
        val listType: Type = object : TypeToken<model.AbilityBonusChoice?>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun abilityBonusChoiceToStoredString(myObjects: model.AbilityBonusChoice?): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToProficiency(data: String?): List<model.Proficiency> {
        if (data == null || data == "null") {
            return emptyList()
        }
        val listType: Type = object : TypeToken<List<model.Proficiency>>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun proficiencyToStoredString(myObjects: List<model.Proficiency>?): String {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToLanguage(data: String?): List<model.Language>? {
        if (data == null) {
            return null
        }
        val listType: Type = object : TypeToken<List<model.Language>?>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun languageToStoredString(myObjects: List<model.Language>?): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToLanguageChoices(data: String?): List<model.LanguageChoice>? {
        if (data == null) {
            return null
        }
        val listType: Type = object : TypeToken<List<model.LanguageChoice>?>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun languageChoicesToStoredString(myObjects: List<model.LanguageChoice>?): String? {
        return gson.toJson(myObjects)
    }


    @TypeConverter
    fun storedStringToSubraces(data: String?): List<model.Subrace>? {
        if (data == null) {
            return null
        }
        val listType: Type = object : TypeToken<List<model.Subrace>?>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun subracesToStoredString(myObjects: List<model.Subrace>?): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToSubrace(data: String?): model.Subrace? {
        if (data == null) {
            return null
        }
        val listType: Type = object : TypeToken<model.Subrace?>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun subraceToStoredString(myObjects: model.Subrace?): String? {
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
    fun storedStringToRace(data: String?): model.Race? {
        if (data == null) {
            return null
        }
        val listType: Type = object : TypeToken<model.Race?>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun raceToStoredString(myObjects: model.Race?): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToResourceList(data: String?): List<model.Resource>? {
        if(data == null) {
            return null
        }
        val listType: Type = object : TypeToken<List<model.Resource>?>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun resourceListToStoredString(myObjects: List<model.Resource>?): String? {
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
    fun storedStringToBackground(data: String?): model.Background? {
        if (data == null) {
            return null
        }
        val listType: Type = object : TypeToken<model.Background?>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun backgroundToStoredString(myObjects: model.Background?): String? {
        return gson.toJson(myObjects)
    }


    @TypeConverter
    fun storedStringToListOfItem(data: String?): List<model.Item> {
        if (data == null) {
            return emptyList()
        }
        val listType: Type = object : TypeToken<List<model.ItemInterface>>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun listOfItemToStoredString(myObjects: List<model.Item>): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToMapOfCurrency(data: String?): Map<String, model.Currency> {
        if (data == null) {
            return emptyMap()
        }
        val listType: Type = object : TypeToken<Map<String, model.Currency>>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun mapOfCurrencyToStoredString(myObjects: Map<String, model.Currency>): String? {
        return gson.toJson(myObjects)
    }




    @TypeConverter
    fun storedStringToListProficiencyChoices(data: String?): List<model.ProficiencyChoice?>? {
        if (data == null) {
            return null
        }
        val listType: Type = object : TypeToken<List<model.ProficiencyChoice?>?>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun listOfProficiencyChoicesToStoredString(myObjects: List<model.ProficiencyChoice?>?): String? {
        return gson.toJson(myObjects)
    }


    @TypeConverter
    fun storedStringToItemChoicesList(data: String?): List<model.ItemChoice> {
        if (data == null) {
            return emptyList()
        }
        val listType: Type = object : TypeToken<List<model.ItemChoice>>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun itemChoicesListChoicesToStoredString(myObjects: List<model.ItemChoice>): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToArmor(data: String?): model.Armor? {
        if (data == null) {
            return null
        }
        val listType: Type = object : TypeToken<model.Armor>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun armorToStoredString(myObjects: model.Armor): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToShield(data: String?): model.Shield? {
        if (data == null) {
            return null
        }
        val listType: Type = object : TypeToken<model.Shield?>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun shieldToStoredString(myObjects: model.Shield?): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToItems(data: String?): List<model.ItemInterface>? {
        return gson.fromJson(data, object : TypeToken<List<model.ItemInterface>?>() {}.type)
    }

    @TypeConverter
    fun itemsToStoredString(myObjects: List<model.ItemInterface>?): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToFeats(data: String?): List<model.Feat> {
        return gson.fromJson(data, object : TypeToken<List<model.Feat>>() {}.type)
    }

    @TypeConverter
    fun featsToStoredString(myObjects: List<model.Feat>): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToPrerequisite(data: String?): model.Prerequisite? {
        return gson.fromJson(data, object : TypeToken<model.Prerequisite?>() {}.type)
    }

    @TypeConverter
    fun prerequisiteToStoredString(myObjects: model.Prerequisite?): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToActivationRequirement(data: String?): model.ActivationRequirement? {
        return gson.fromJson(data, object : TypeToken<model.ActivationRequirement?>() {}.type)
    }

    @TypeConverter
    fun activationRequirementToStoredString(myObjects: model.ActivationRequirement?): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToScalingBonus(data: String?): model.ScalingBonus? {
        return gson.fromJson(data, object : TypeToken<model.ScalingBonus?>() {}.type)
    }

    @TypeConverter
    fun scalingBonusToStoredString(myObjects: model.ScalingBonus?): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToSpells(data: String?): List<model.Spell>?  {
        return gson.fromJson(data, object : TypeToken<List<model.Spell>?>() {}.type)
    }

    @TypeConverter
    fun spellsToStoredString(myObjects: List<model.Spell>?): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToInfusion(data: String?): model.Infusion? {
        return gson.fromJson(data, object : TypeToken<model.Infusion?>() {}.type)
    }

    @TypeConverter
    fun infusionToStoredString(myObjects: model.Infusion?): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToChoose(data: String?): model.Choose? {
        return gson.fromJson(data, object : TypeToken<model.Choose?>() {}.type)
    }

    @TypeConverter
    fun chooseToStoredString(myObjects: model.Choose?): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToArmorClass(data: String?): model.ArmorClass? {
        return gson.fromJson(data, object : TypeToken<model.ArmorClass?>() {}.type)
    }

    @TypeConverter
    fun armorClassToStoredString(myObjects: model.ArmorClass?): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToFeatureChoices(data: String?): List<model.FeatureChoice>? {
        return gson.fromJson(data, object : TypeToken<List<model.FeatureChoice>?>() {}.type)
    }

    @TypeConverter
    fun featureChoicesToStoredString(myObjects: List<model.FeatureChoice>?): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToResource(data: String?): model.Resource? {
        return gson.fromJson(data, object : TypeToken<model.Resource?>() {}.type)
    }

    @TypeConverter
    fun resourceChoicesToStoredString(myObjects: model.Resource?): String? {
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
    fun storedStringToFeatChoiceList(data: String?): MutableList<model.FeatChoice> {
        return gson.fromJson(data, object : TypeToken<MutableList<model.FeatChoice>>() {}.type)
    }

    @TypeConverter
    fun featChoiceListToStoredString(myObjects: MutableList<model.FeatChoice>): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToListPairIntSpell(data: String?): List<Pair<Int, model.Spell>>? {
        return gson.fromJson(data, object : TypeToken<List<Pair<Int, model.Spell>>?>() {}.type)
    }

    @TypeConverter
    fun listPairIntSpellToStoredString(myObjects: List<Pair<Int, model.Spell>>?): String? {
        return gson.toJson(myObjects)
    }
}