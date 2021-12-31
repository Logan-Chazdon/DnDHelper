package com.example.dndhelper.repository.dataClasses

import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.Class
import java.lang.reflect.Type
import java.util.HashMap




class ItemDeserializer(private val itemTypeElementName: String) :
    JsonDeserializer<ItemInterface?> {
    private val gson: Gson = Gson()
    private val itemTypeRegistry: MutableMap<String, Class<out ItemInterface?>>
    fun registerItemType(itemTypeName: String, itemType: Class<out ItemInterface?>) {
        itemTypeRegistry[itemTypeName] = itemType
    }

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): ItemInterface? {
        val itemObject = json.asJsonObject
        val itemTypeElement = itemObject[itemTypeElementName]
        val itemType: Class<out ItemInterface?>? = itemTypeRegistry[itemTypeElement.asString]
        return gson.fromJson(itemObject, itemType)
    }

    init {
        itemTypeRegistry = HashMap()
    }
}

