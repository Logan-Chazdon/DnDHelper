package com.example.dndhelper.repository.dataClasses

data class Feature(
    val name: String,
    val description: String,
    val level: Int = 1,
    val choiceNum: Int = 0,
    val options: MutableList<Feature>?,
    val prerequisite: Prerequisite? = null
) {
    var chosen : List<Feature>? = null
    var resource: Resource? = null

    fun recharge(basis: Int) {
        resource?.recharge(basis)
        chosen?.forEach {
            it.resource?.recharge(basis)
        }
    }

    fun getAvailableOptions(
        character: Character?,
        assumedProficiencies: List<Proficiency>
    ): MutableList<Feature> {
        val result = mutableListOf<Feature>()
        options?.forEach {
            if(it.prerequisite?.check(character, assumedProficiencies) != false) {
                result.add(it)
            }
        }
        return result
    }

}