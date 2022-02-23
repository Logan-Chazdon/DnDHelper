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
    val range: String?,
    val properties : List<Property>? = null,
    val isMartial: Boolean = false,
    override val charges: Resource? = null,
    override val maxInfusions: Int? = 1,
    override val infusions: MutableList<Infusion>? = mutableListOf()
) : ItemInterface {
    override val type = "Weapon"

    val damageDesc: String
    get() {
        val infusionBonus = infusions.let { infusions ->
            var result = 0
            infusions?.forEach {
                it.currentAtkDmgBonus?.let { bonus ->
                    result += bonus
                }
            }
            result
        }
        return if(infusionBonus == 0)
            "$damage $damageType"
        else
            "$damage + $infusionBonus $damageType"
    }
}