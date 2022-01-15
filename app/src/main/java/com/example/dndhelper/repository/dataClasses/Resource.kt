package com.example.dndhelper.repository.dataClasses

class Resource(
    val name: String,
    var currentAmount: Int = 0,
    val maxAmount: (Int) -> Int,
    val rechargeAmount: (() -> Int)?,
) {
    fun recharge(
        maxAmountBasis: Int
    ) {
        ((rechargeAmount?.invoke() ?: maxAmount(maxAmountBasis)) + currentAmount).let {
            currentAmount = if(it > maxAmount(maxAmountBasis)) {
                maxAmount(maxAmountBasis)
            } else {
                it
            }
        }
    }
}
