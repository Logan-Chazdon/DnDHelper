package com.example.dndhelper.repository.model

import androidx.lifecycle.MutableLiveData
import androidx.room.TypeConverter
import com.example.dndhelper.repository.dataClasses.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*

class Converters {
    @TypeConverter
    fun storedStringToClasses(data: String?): MutableList<Class> {
        val gson = Gson()
        if (data == null) {
            return mutableListOf()
        }
        val listType: Type = object : TypeToken<MutableList<Class>>() {}.getType()
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun classesToStoredString(myObjects: Class): String? {
        val gson = Gson()
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToClass(data: String?): Class {
        val gson = Gson()
        if (data == null) {
            return Class(name = "My Character")
        }
        val listType: Type = object : TypeToken<Class>() {}.getType()
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun classToStoredString(myObjects: MutableList<Class>): String? {
        val gson = Gson()
        return gson.toJson(myObjects)
    }


    @TypeConverter
    fun storedStringToSubClass(data: String?): MutableList<Subclass> {
        val gson = Gson()
        if (data == null) {
            return mutableListOf()
        }
        val listType: Type = object : TypeToken<MutableList<Subclass>>() {}.getType()
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun subClassToStoredString(myObjects: MutableList<Subclass>): String? {
        val gson = Gson()
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToClassLevelMapChoices(data: String?): MutableMap<Int, String> {
        val gson = Gson()
        if (data == null) {
            return mutableMapOf()
        }
        val listType: Type = object : TypeToken< MutableMap<Int, String>>() {}.getType()
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun classLevelMapChoicesToStoredString(myObjects: MutableMap<Int, String>): String? {
        val gson = Gson()
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToClassLevelTable(data: String?): Array<Array<String>> {
        val gson = Gson()
        if (data == null) {
            return arrayOf()
        }
        val listType: Type = object : TypeToken< Array<Array<String>>>() {}.getType()
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun classLevelTableToStoredString(myObjects: Array<Array<String>>): String? {
        val gson = Gson()
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToLevelPath(data: String?): MutableList<Feature> {
        val gson = Gson()
        if (data == null) {
            return mutableListOf()
        }
        val listType: Type = object : TypeToken<MutableList<Feature>>() {}.getType()
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun levelPathToStoredString(myObjects: MutableList<Feature>): String? {
        val gson = Gson()
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToFeature(data: String?): Feature? {
        val gson = Gson()
        if (data == null) {
            return null
        }
        val listType: Type = object : TypeToken<Feature>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun featureToStoredString(myObjects: Feature): String? {
        val gson = Gson()
        return gson.toJson(myObjects)
    }

    //Races
    @TypeConverter
    fun storedStringToAbilityBonus(data: String?): List<AbilityBonus>? {
        val gson = Gson()
        if (data == null) {
            return listOf()
        }
        val listType: Type = object : TypeToken<List<AbilityBonus>>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun abilityBonusToStoredString(myObjects: List<AbilityBonus>): String? {
        val gson = Gson()
        return gson.toJson(myObjects)
    }


    @TypeConverter
    fun storedStringToProficiency(data: String?): List<Proficiency>? {
        val gson = Gson()
        if (data == null) {
            return null
        }
        val listType: Type = object : TypeToken<List<Proficiency>>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun proficiencyToStoredString(myObjects: List<Proficiency>): String? {
        val gson = Gson()
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToLanguage(data: String?): List<Language>? {
        val gson = Gson()
        if (data == null) {
            return null
        }
        val listType: Type = object : TypeToken<List<Language>>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun languageToStoredString(myObjects: List<Language>): String? {
        val gson = Gson()
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToSubrace(data: String?): List<Subrace>? {
        val gson = Gson()
        if (data == null) {
            return null
        }
        val listType: Type = object : TypeToken<List<Subrace>>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun subraceToStoredString(myObjects: List<Subrace>): String? {
        val gson = Gson()
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToRace(data: String?): Race? {
        val gson = Gson()
        if (data == null) {
            return null
        }
        val listType: Type = object : TypeToken<Race?>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun raceToStoredString(myObjects: Race?): String? {
        val gson = Gson()
        return gson.toJson(myObjects)
    }

    @TypeConverter
    fun storedStringToStringIntMap(data: String?): MutableMap<String, Int> {
        val gson = Gson()
        if (data == null) {
            return mutableMapOf()
        }
        val listType: Type = object : TypeToken<MutableMap<String, Int>?>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun stringIntMapToStoredString(myObjects: MutableMap<String, Int>?): String? {
        val gson = Gson()
        return gson.toJson(myObjects)
    }

}