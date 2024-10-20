package model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
@SerialName("Item")
data class Item (
    override var name : String? = null,
    override val index: String? = null,
    override var desc: String? = null,
    override val itemRarity : String? = null,
    override val cost : Map<String, Currency>? = null,
    override val weight : Int? = 0,
    override val charges: Resource? = null,
    override val maxInfusions: Int? = 1,
    override val infusions: MutableList<Infusion>? = mutableListOf()
) : ItemInterface {
    //It's not transient for GSON on android only for kotlinx.
    @Transient
    override val type = "Item"
    override val displayName: String
        get() = name ?: ""
}
