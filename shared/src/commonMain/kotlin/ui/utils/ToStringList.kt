package ui.utils


import model.AbilityBonus
import model.LanguageChoice
import model.ProficiencyChoice
import kotlin.jvm.JvmName

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