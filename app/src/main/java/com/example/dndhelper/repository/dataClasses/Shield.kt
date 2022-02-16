package com.example.dndhelper.repository.dataClasses

data class Shield(
    override val name: String?,
    override val index: String?,
    override val desc: String?,
    override val itemRarity: String?,
    override val cost: Map<String, Currency>?,
    override val weight: Int?,
    override val charges: Resource?,
    val acBonus: Int = 2,
    override val maxInfusions: Int? = 1,
    override val infusions: MutableList<Infusion>? = mutableListOf()
) : ItemInterface {
    override val type = "Shield"

     val totalAcBonus : Int
     get() {
        var result = acBonus
        infusions?.forEach { infusion ->
            infusion.currentAcBonus?.let {
                result += it
            }
        }
        return result
    }
}