package gmail.loganchazdon.dndhelper.model



data class Item (
    override val name : String? = null,
    override val index: String? = null,
    override val desc: String? = null,
    override val itemRarity : String? = null,
    override val cost : Map<String, Currency>? = null,
    override val weight : Int? = 0,
    override val charges: Resource? = null,
    override val maxInfusions: Int? = 1,
    override val infusions: MutableList<Infusion>? = mutableListOf()
) : ItemInterface {
    override val type = "Item"
    override val displayName: String
        get() = name ?: ""
}
