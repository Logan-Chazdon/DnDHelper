package com.example.dndhelper.repository.dataClasses

data class Prerequisite(
    val proficiency: Proficiency? = null
) {
    fun check(character: Character?, assumedProficiencies: List<Proficiency>) : Boolean {
        proficiency?.name?.let {
            if(
                character?.checkForProficiency(it) == true ||
                assumedProficiencies.find {prof -> prof.name == it } == null
            ) {
                return false
            }
        }
        return true
    }
}
