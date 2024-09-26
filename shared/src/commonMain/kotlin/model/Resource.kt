package model

data class Resource(
    val name: String,
    var currentAmount: Int = 0,
    var maxAmountType: String,
    var rechargeAmountType: String,
) {
    fun maxAmount(maxAmountBasis: Int? = null) : Int {
        return when(maxAmountType) {
            "1:1" -> {
                maxAmountBasis!!
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
