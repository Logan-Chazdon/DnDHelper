package com.example.dndhelper.repository.dataClasses

data class Shield(
    override val name: String?,
    override val index: String?,
    override val desc: String?,
    override val itemRarity: String?,
    override val cost: Map<String, Currency>?,
    override val weight: Int?,
    override val charges: Resource?,
    val acBonus: Int = 2
) : ItemInterface {
    override val type = "Shield"
}