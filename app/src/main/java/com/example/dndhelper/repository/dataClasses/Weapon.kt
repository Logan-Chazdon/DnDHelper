package com.example.dndhelper.repository.dataClasses


data class Weapon(
    override val name : String? = null,
    override val index: String? = null,
    override val desc: String? = null,
    override val itemRarity : String? = null,
    override val weight: Int? = 0,
    override val cost : List<Currency>? = null,
    val damage : String,
    val damageType: String,
    val range: String,
    val properties : List<Property>? = null,
) : ItemInterface {
    override val type = "Weapon"
}