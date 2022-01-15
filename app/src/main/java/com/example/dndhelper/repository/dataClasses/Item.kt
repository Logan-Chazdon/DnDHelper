package com.example.dndhelper.repository.dataClasses



data class Item (
    override val name : String? = null,
    override val index: String? = null,
    override val desc: String? = null,
    override val itemRarity : String? = null,
    override val cost : Map<String, Currency>? = null,
    override val weight : Int? = 0,
    override val charges: Resource? = null,
) : ItemInterface{
    override val type = "Item"
}
