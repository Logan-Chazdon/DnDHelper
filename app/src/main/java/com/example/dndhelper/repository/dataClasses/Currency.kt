package com.example.dndhelper.repository.dataClasses

import androidx.compose.runtime.Composable


data class Currency(
    var amount: Int,
    override val name: String?,
    override val weight: Int? = 0
) : ItemInterface {
   override val type = "Currency"

    override val index: String? = null
    override val desc: String? = null
    override val itemRarity: String? = null
    override val cost: List<Currency>? = null

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

    public fun subtractInCopper(copper: Int) {
        amount -= copper / currencyValues[abbreviatedName]!!
    }

    companion object {
        private val currencyValues = mapOf(
            "cp" to 1,
            "sp" to 10,
            "ec" to 50,
            "gp" to 100,
            "pp" to 1000
        )

        fun getEmptyCurrencyList(): List<Currency> {
            return listOf(
                Currency(
                    name = "Platinum pieces",
                    amount = 0
                ),
                Currency(
                    name = "Gold pieces",
                    amount = 0
                ),
                Currency(
                    name = "Electrum pieces",
                    amount = 0
                ),
                Currency(
                    name = "Silver pieces",
                    amount = 0
                ),
                Currency(
                    name = "Copper pieces",
                    amount = 0
                )
            )
        }
    }
}