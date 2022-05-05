package com.example.dndhelper.model.database

import com.example.dndhelper.model.ItemInterface
import com.google.gson.*
import java.lang.reflect.Type


class ItemConverter(private val itemTypeElementName: String) :
    JsonDeserializer<ItemInterface>,
    JsonSerializer<ItemInterface>{
    private val gson: Gson = Gson()
    private val itemTypeRegistry: MutableMap<String, Class<out ItemInterface>>
    fun registerItemType(itemTypeName: String, itemType: Class<out ItemInterface>) {
        itemTypeRegistry[itemTypeName] = itemType
    }

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): ItemInterface {
        val itemObject = json.asJsonObject
        val itemTypeElement = itemObject[itemTypeElementName]
        val itemType: Class<out ItemInterface>? = itemTypeRegistry[itemTypeElement.asString]
        return gson.fromJson(itemObject, itemType)
    }


    override fun serialize(
        src: ItemInterface?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return gson.toJsonTree(src)
    }


    init {
        itemTypeRegistry = HashMap()
    }


}

