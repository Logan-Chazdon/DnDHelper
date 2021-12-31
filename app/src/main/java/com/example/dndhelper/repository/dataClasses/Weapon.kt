package com.example.dndhelper.repository.dataClasses

data class Weapon(
    val damage : String,
    val damageType: String,
    val range: String,
    val properties : List<Property>? = null
) : Item()