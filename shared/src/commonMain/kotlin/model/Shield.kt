package model

data class Shield(
    override var name: String?,
    override val index: String?,
    override var desc: String?,
    override val itemRarity: String?,
    override val cost: Map<String, Currency>?,
    override val weight: Int?,
    override val charges: Resource?,
    var acBonus: Int = 2,
    override val maxInfusions: Int? = 1,
    override val infusions: MutableList<Infusion>? = mutableListOf()
) : ItemInterface {
    override val type = "Shield"
    override val displayName: String
        get() = name ?: ""
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