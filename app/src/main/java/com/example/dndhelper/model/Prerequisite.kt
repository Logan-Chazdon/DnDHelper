package com.example.dndhelper.model

data class Prerequisite(
    val proficiency: Proficiency? = null,
    val level: Int? = null,
    val feature: String? = null,
    val spell: String? = null,
    val hasSpells: Boolean? = null,
    val isCaster: Boolean? = null,
    val stats: Map<String, Int>? = null
) {
    fun check(
        character: Character?,
        assumedFeatures: List<Feature>,
        assumedProficiencies: List<Proficiency>?,
        assumedClass: Class?,
        assumedSpells: List<Spell>,
        assumedLevel: Int?,
        assumedStatBonuses: Map<String, Int>?
    ) : Boolean {
        proficiency?.name?.let {
            if(
                character?.checkForProficiencyOrExpertise(it) != 0 &&
                assumedProficiencies?.find {prof -> prof.name == it } == null
            ) {
                return false
            }
        }

        hasSpells?.let {
            if(
                character?.hasSpells != it
                && assumedClass?.pactMagic == null
                && assumedClass?.spellCasting?.type != 0.0
            ) {
                return false
            }
        }

        isCaster?.let {
            if(
                character?.isCaster != it
                && assumedClass?.pactMagic == null
                && assumedClass?.spellCasting?.type != 0.0
            ) {
                return false
            }
        }

        spell?.let {
            if(!checkSpell(assumedSpells, character)) {
                return false
            }
        }


        level?.let {
            if(assumedLevel != null) {
                if(assumedLevel < level) {
                    return false
                }
            } else {
                if(character?.totalClassLevels ?: 0 < level) {
                    return false
                }
            }
        }

        stats?.let {
            it.forEach { (statName, stat) ->
                if(
                    (character?.getStat(statName) ?: 0) +
                    (assumedStatBonuses?.getOrDefault(statName, 0) ?: 0)
                    < stat
                ) {
                    return false
                }
            }
        }

        feature?.let {
            if(!checkFeature(character, assumedFeatures)) {
                return false
            }
        }

        return true
    }

    private fun checkFeature(character: Character?, assumedFeatures: List<Feature>): Boolean {
        fun checkForFeature(it: Feature) : Boolean {
            if(it.name == feature) {
                return true
            }
            it.chosen?.forEach {
                if(checkForFeature(it)) {
                    return true
                }
            }
            return false
        }

        assumedFeatures.forEach {
            if(checkForFeature(it)) {
                return true
            }
        }

        character?.features?.forEach {
            if(checkForFeature(it.second)) {
                return true
            }
        }

        return false
    }

    private fun checkSpell(
        assumedSpells : List<Spell>?,
        character: Character?
    ) : Boolean {
        character?.additionalSpells?.forEach { level ->
            level.value.forEach { (_, it) ->
                if(it.name == spell) {
                    return true
                }
            }
        }
        assumedSpells?.forEach {
            if(it.name == spell) {
                return true
            }
        }
        return false
    }
}
