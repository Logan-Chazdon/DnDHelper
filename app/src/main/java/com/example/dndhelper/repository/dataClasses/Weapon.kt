package com.example.dndhelper.repository.dataClasses


data class Weapon(
    override val name : String? = null,
    override val index: String? = null,
    override val desc: String? = null,
    override val itemRarity : String? = null,
    override val weight: Int? = 0,
    override val cost : Map<String, Currency>? = null,
    val damage : String,
    val damageType: String,
    val range: String,
    val properties : List<Property>? = null,
    val isMartial: Boolean = false,
    override val charges: Resource? = null
) : ItemInterface {
    override val type = "Weapon"

    val damageDesc: String
    get() {
        return "$damage $damageType"
    }
}