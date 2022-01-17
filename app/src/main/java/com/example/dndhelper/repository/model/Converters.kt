package com.example.dndhelper.repository.model

import androidx.room.TypeConverter
import com.example.dndhelper.repository.dataClasses.*
import com.example.dndhelper.repository.dataClasses.Currency
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class Converters {
    var gson = Gson()
    init {
        val converter = ItemConverter("type")
        converter.registerItemType("Item", Item::class.java)
        converter.registerItemType("Weapon", Weapon::class.java)
        converter.registerItemType("Armor", Armor::class.java)
        converter.registerItemType("Currency", Currency::class.java)
        gson = GsonBuilder()
            .registerTypeAdapter(ItemInterface::class.java, converter)
            .create()

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
    fun storedStringToBackpack(data: String?): Backpack? {
        if (data == null ) {
            return null
        }
        val listType: Type = object : TypeToken<Backpack?>() {}.getType()
        return gson.fromJson(data, listType)
    }


    @TypeConverter
    fun backpackToStoredString(myObjects: Backpack?): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToSubClass(data: String?): MutableList<Subclass> {
        if (data == null) {
            return mutableListOf()
        }
        val listType: Type = object : TypeToken<MutableList<Subclass>>() {}.getType()
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun subClassToStoredString(myObjects: MutableList<Subclass>): String? {
        return gson.toJson(myObjects)
    }



    @TypeConverter
    fun storedStringToLevelPath(data: String?): MutableList<Feature> {
        if (data == null) {
            return mutableListOf()
        }
        val listType: Type = object : TypeToken<MutableList<Feature>>() {}.getType()
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun levelPathToStoredString(myObjects: MutableList<Feature>): String? {
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
    fun abilityBonusToStoredString(myObjects: List<AbilityBonus>): String? {
        return gson.toJson(myObjects)
    }


    @TypeConverter
    fun storedStringToProficiency(data: String?): List<Proficiency>? {
        if (data == null) {
            return null
        }
        val listType: Type = object : TypeToken<List<Proficiency>>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun proficiencyToStoredString(myObjects: List<Proficiency>): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToLanguage(data: String?): List<Language>? {
        if (data == null) {
            return null
        }
        val listType: Type = object : TypeToken<List<Language>>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun languageToStoredString(myObjects: List<Language>): String? {
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToSubrace(data: String?): List<Subrace>? {
        if (data == null) {
            return null
        }
        val listType: Type = object : TypeToken<List<Subrace>>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun subraceToStoredString(myObjects: List<Subrace>): String? {
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
    fun stringListToStoredString(myObjects: List<String>): String? {
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
    fun storedStringToItems(data: String?): List<ItemInterface> {
        return gson.fromJson(data, object : TypeToken<List<ItemInterface>>() {}.type)
    }

    @TypeConverter
    fun itemsToStoredString(myObjects: List<ItemInterface>): String? {
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

}