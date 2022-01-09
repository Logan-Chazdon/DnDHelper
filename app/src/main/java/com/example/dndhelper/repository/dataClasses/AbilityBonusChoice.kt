package com.example.dndhelper.repository.dataClasses

import com.example.dndhelper.repository.dataClasses.AbilityBonus

data class AbilityBonusChoice(
    val choose: Int,
    val from : List<AbilityBonus>
) {
    var chosen: List<AbilityBonus>? = null
}
