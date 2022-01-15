package com.example.dndhelper.repository.dataClasses

import kotlin.math.ceil


data class Currency(
    var amount: Int,
    override val name: String?,
    override val weight: Int? = 0,
) : ItemInterface {
    override val type = "Currency"
    override val charges: Resource? = null
    override val index: String? = null
    override val desc: String? = null
    override val itemRarity: String? = null
    override val cost: Map<String, Currency>? = null

    public val abbreviatedName : String
    get() {
        if(name?.isNotEmpty() == true) {
            var it = ""
            for (s in name.split(" ")) {
                it += s[0].lowercase()
            }
            return it
        }
        return ""
    }


    public val getValueInCopper : Int
    get() {
        //Return the value of the currency times the amount
        if(currencyValues.containsKey(abbreviatedName)) {
            return currencyValues[abbreviatedName]!! * amount
        }

        //In case we have an unknown currency type
        return 0
    }


    public fun subtractInCopper(copper: Int) : Int {
            amount -= ceil(copper.toDouble() / currencyValues[abbreviatedName]!!).toInt()
            if(copper == 0) {
                return 0
            }
            return copper % currencyValues[abbreviatedName]!!
    }

    companion object {
        private val currencyValues = mapOf(
            "cp" to 1,
            "sp" to 10,
            "ep" to 50,
            "gp" to 100,
            "pp" to 1000
        )

        fun getEmptyCurrencyMap(): Map<String, Currency> {
            return mapOf(
                "pp" to Currency(
                    name = "Platinum pieces",
                    amount = 0
                ),
                "gp" to  Currency(
                    name = "Gold pieces",
                    amount = 0
                ),
                "ep" to Currency(
                    name = "Electrum pieces",
                    amount = 0
                ),
                "sp" to Currency(
                    name = "Silver pieces",
                    amount = 0
                ),
                "cp" to Currency(
                    name = "Copper pieces",
                    amount = 0
                )
            )
        }
    }
}