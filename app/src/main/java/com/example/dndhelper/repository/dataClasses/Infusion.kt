package com.example.dndhelper.repository.dataClasses

data class Infusion(
    val name: String,
    val desc: String,
    val targetItemFilter: TargetItemFilter?,
    val charges: Resource? = null,
    val acBonus: ScalingBonus? = null,
    val atkDmgBonus: ScalingBonus? = null,
    var active: Boolean = false,
    val attuned: Boolean,
    var level: Int = 0,
    val grantedAtLevel: Int
) {
    val currentAcBonus: Int?
    get() {
        return acBonus?.calculate(level)
    }

    val currentAtkDmgBonus: Int?
    get() {
        return atkDmgBonus?.calculate(level)
    }
}
