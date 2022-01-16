package com.example.dndhelper.repository.dataClasses

data class Armor(
    override val name : String? = null,
    override val index: String? = null,
    override val desc: String? = null,
    override val itemRarity : String? = null,
    override val cost : Map<String, Currency>? = null,
    override val weight : Int? = null,
    val baseAc : Int,
    val dexCap: Int,
    val stealth: String,
    override val charges: Resource? = null,
    val strengthPrerequisite: Int? = null
) : ItemInterface {
    override val type = "Armor"

    fun getAC(dexMod: Int) : Int{
        return baseAc + dexMod(dexMod)
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
            dexCap == 0 -> "$baseAc"
            dexCap < 5 -> "$baseAc+Dex mod(max $dexCap)"
            else -> "$baseAc+Dex mod"
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


