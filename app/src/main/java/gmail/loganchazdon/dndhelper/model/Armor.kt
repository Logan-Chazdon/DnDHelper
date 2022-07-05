package gmail.loganchazdon.dndhelper.model

data class Armor(
    override var name : String? = null,
    override val index: String? = null,
    override var desc: String? = null,
    override val itemRarity : String? = null,
    override val cost : Map<String, Currency>? = null,
    override val weight : Int? = null,
    var baseAc : Int,
    val dexCap: Int,
    var stealth: String,
    override val charges: Resource? = null,
    val strengthPrerequisite: Int? = null,
    override val maxInfusions: Int? = 1,
    override val infusions: MutableList<Infusion>? = mutableListOf()
) : ItemInterface {
    override val type = "Armor"
    override val displayName: String
        get() = name ?: ""

    val totalBaseAc : Int
    get() {
        var result = baseAc
        infusions?.forEach { infusion ->
            infusion.currentAcBonus?.let {
                result += it
            }
        }
        return result
    }

    fun getAC(dexMod: Int) : Int{
        return totalBaseAc + dexMod(dexMod)
    }

    private fun dexMod(dexMod: Int): Int{
        return if(dexMod <= dexCap){
            dexMod
        } else {
            dexCap
        }
    }

    val acDesc: String
    get() {
        return when {
            dexCap == 0 -> "$totalBaseAc"
            dexCap < 5 -> "$totalBaseAc+Dex mod(max $dexCap)"
            else -> "$totalBaseAc+Dex mod"
        }
    }

    companion object {
        val none = Armor(
            name = "none",
            baseAc = 10,
            dexCap = 5,
            stealth = "-"
        )
    }

}


