package com.example.dndhelper.repository.dataClasses

open class Item (
    val name : String? = null,
    val index: String? = null,
    val desc: String? = null,
    val itemRarity : String? = null,
    val cost : List<Currency>? = null,
    val weight : Int = 0
)
