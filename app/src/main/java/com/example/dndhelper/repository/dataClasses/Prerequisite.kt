package com.example.dndhelper.repository.dataClasses

data class Prerequisite(
    val proficiency: Proficiency? = null,
    val level: Int? = null,
    val feature: String? = null,
    val spell: String? = null
) {
    fun check(character: Character?, assumedProficiencies: List<Proficiency>) : Boolean {
        proficiency?.name?.let {
            if(
                character?.checkForProficiencyOrExpertise(it) != 0 ||
                assumedProficiencies.find {prof -> prof.name == it } == null
            ) {
                return false
            }
        }

        //TODO check spell

        //TODO check level

        // TODO check feature
        return true
    }
}
