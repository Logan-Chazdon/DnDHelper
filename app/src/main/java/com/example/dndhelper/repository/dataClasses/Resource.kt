package com.example.dndhelper.repository.dataClasses

class Resource(
    val name: String,
    var currentAmount: Int = 0,
    val maxAmountType: String,
    val rechargeAmountType: String,
) {
    private fun maxAmount(maxAmountBasis: Int) : Int {
        return when(maxAmountType) {
            "1:1" -> {
                maxAmountBasis
            }
            else -> maxAmountType.toInt()
        }
    }

    private fun rechargeAmount() : Int? {
        return when(rechargeAmountType) {
            else -> null
        }
    }
    fun recharge(
        maxAmountBasis: Int
    ) {
        ((rechargeAmount() ?: maxAmount(maxAmountBasis)) + currentAmount).let {
            currentAmount = if(it > maxAmount(maxAmountBasis)) {
                maxAmount(maxAmountBasis)
            } else {
                it
            }
        }
    }
}
