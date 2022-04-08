package com.example.dndhelper.repository.dataClasses

data class ArmorClass(
    private val base : Int,
    private val dexMax: Int,
    private val wisMax: Int,
    private val conMax: Int
) {
    fun calculate(
        dex: Int,
        con: Int,
        wis: Int
    ) : Int {
        var result = base
        result += calcBonus(dexMax, dex)
        result += calcBonus(conMax, con)
        result += calcBonus(wisMax, wis)
        return result
    }

    private fun calcBonus(max: Int, stat: Int): Int {
        return  maxOf(minOf(max, stat), 0)
    }
}
