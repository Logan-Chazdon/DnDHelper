package gmail.loganchazdon.dndhelper.ui.utils

import gmail.loganchazdon.dndhelper.model.AbilityBonus
import gmail.loganchazdon.dndhelper.model.LanguageChoice
import gmail.loganchazdon.dndhelper.model.ProficiencyChoice

fun List<ProficiencyChoice>.toStringList(): List<List<String>> {
    val result = mutableListOf<List<String>>()
    forEach {
        result.add(
            it.chosenByString
        )
    }
    return result
}

@JvmName("toStringListAbilityBonus")
fun List<AbilityBonus>.toStringList(): List<String> {
    val result = mutableListOf<String>()
    forEach {
        result.add(
            it.toString()
        )
    }
    return result
}

@JvmName("toStringListLanguageChoice")
fun List<LanguageChoice>.toStringList(): List<List<String>> {
    val result = mutableListOf<List<String>>()
    forEach {
        result.add(
            it.chosenByString
        )
    }
    return result
}