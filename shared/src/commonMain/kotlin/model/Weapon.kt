package model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
@SerialName("Weapon")
data class Weapon(
    override var name : String? = null,
    override val index: String? = null,
    override var desc: String? = null,
    override val itemRarity : String? = null,
    override val weight: Int? = 0,
    override val cost : Map<String, Currency>? = null,
    var damage : String,
    val damageType: String,
    val range: String?,
    val properties : List<Property>? = null,
    val proficiency : String,
    override val charges: Resource? = null,
    override val maxInfusions: Int? = 1,
    override val infusions: MutableList<Infusion>? = mutableListOf()
) : ItemInterface {
    //It's not transient for GSON on android only for kotlinx.
    @Transient
    override val type = "Weapon"
    override val displayName: String
        get() = name ?: ""
    val damageDesc: String
    get() {
        val infusionBonus = getInfusionBonus()
        return if(infusionBonus == 0)
            "$damage $damageType"
        else
            "$damage + $infusionBonus $damageType"
    }

    fun getInfusionBonus() : Int {
        return infusions.let { infusions ->
            var result = 0
            infusions?.forEach {
                it.currentAtkDmgBonus?.let { bonus ->
                    result += bonus
                }
            }
            result
        }
    }
}