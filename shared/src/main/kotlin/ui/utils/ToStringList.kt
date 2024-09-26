package ui.utils


import model.AbilityBonus
import model.LanguageChoice
import model.ProficiencyChoice

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