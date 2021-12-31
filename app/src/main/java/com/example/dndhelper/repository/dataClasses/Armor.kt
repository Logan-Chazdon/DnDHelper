package com.example.dndhelper.repository.dataClasses

class Armor(
    name : String? = null,
    index: String? = null,
    desc: String? = null,
    itemRarity : String? = null,
    cost : List<Currency>? = null,
    weight : Int,
    val baseAc : Int,
    val dexCap: Int,
    val stealth: String
) : Item(
    name = name,
    index = index,
    desc = desc,
    itemRarity = itemRarity,
    cost = cost,
    weight = weight
) {
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
}