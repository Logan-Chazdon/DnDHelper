package com.example.dndhelper.repository.dataClasses

class Weapon(
    name : String? = null,
    index: String? = null,
    desc: String? = null,
    itemRarity : String? = null,
    cost : List<Currency>? = null,
    val damage : String,
    val damageType: String,
    val range: String,
    val properties : List<Property>? = null
) : Item(name, index, desc, itemRarity, cost)