package com.example.dndhelper.model

interface ItemInterface {
    val type : String
    val displayName: String
    val name : String?
    val index: String?
    val desc: String?
    val itemRarity : String?
    val cost : Map<String, Currency>?
    val weight : Int?
    val charges: Resource?
    val maxInfusions: Int?
    val infusions: MutableList<Infusion>?

    fun costString() : String {
        var result = ""
        cost?.forEach {
            if(it.value.amount != 0)
                result += "${it.value.amount} ${it.value.abbreviatedName}"
        }
        return result
    }

    fun hasCost() : Boolean {
        if(cost.isNullOrEmpty()) {
            return false
        }
        cost?.forEach {
            if(it.value.amount != 0) {
                return true
            }
        }
        return false
    }
}
