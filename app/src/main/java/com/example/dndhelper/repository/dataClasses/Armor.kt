package com.example.dndhelper.repository.dataClasses

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName

data class Armor(
    override val name : String? = null,
    override val index: String? = null,
    override val desc: String? = null,
    override val itemRarity : String? = null,
    override val cost : List<Currency>? = null,
    override val weight : Int? = null,
    val baseAc : Int,
    val dexCap: Int,
    val stealth: String
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

    companion object {
        val none = Armor(
            name = "none",
            baseAc = 10,
            dexCap = 5,
            stealth = "-"
        )
    }

}


