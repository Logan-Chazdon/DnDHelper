package com.example.dndhelper.model

data class Feature(
    val name: String,
    val description: String,
    val index : String? = null, //This is used when we need to check for a specific feature. For example when a subrace overrides a race.
    val grantedAtLevel: Int = 1,
    val choose: Choose = Choose(0),
    val options: MutableList<Feature>?,
    val prerequisite: Prerequisite? = null,
    val spells: List<Spell>? = null, //Spells granted by this feature
    val infusion: Infusion? = null,
    val maxActive: Choose = Choose(0),
    val hpBonusPerLevel: Int? = null,
    val acBonus: Int? = null,
    val ac: ArmorClass? = null, //This will only be applied when not wearing armor.
    val proficiencies: List<Proficiency>? = null,
    val languages: List<Language>? = null,
) {

    var chosen : List<Feature>? = null
    var resource: Resource? = null

    val grantsInfusions: Boolean
    get() {
        if(infusion != null) {
            return true
        }
        chosen?.forEach {
            if(it.grantsInfusions) {
                return true
            }
        }
        return false
    }

    fun recharge(basis: Int) {
        resource?.recharge(basis)
        chosen?.forEach {
            it.resource?.recharge(basis)
        }
    }

    fun getAvailableOptions(
        character: Character?,
        assumedProficiencies: List<Proficiency>,
        level: Int,
        assumedClass: Class?,
        assumedSpells: List<Spell>,
        assumedStatBonuses: Map<String, Int>?
    ): MutableList<Feature> {
        val result = mutableListOf<Feature>()

        options?.forEach {
            if(
                try {level >= it.grantedAtLevel} catch(e: NumberFormatException) {false} &&
                it.prerequisite?.check(
                    character,
                    assumedProficiencies,
                    assumedClass = assumedClass,
                    assumedLevel = level,
                    assumedStatBonuses = assumedStatBonuses,
                    assumedSpells = assumedSpells
                ) != false
            ) {
                result.add(it)
            }
        }
        return result
    }

    //Returns all spells granted bu the feature in its current state.
    fun getSpellsGiven(): List<Spell> {
        val result = mutableListOf<Spell>()
        spells?.let { result.addAll(it) }
        chosen?.forEach {
            it.spells?.let { spells -> result.addAll(spells) }
        }
        return result
    }

    val currentActive: Int
    get() {
        var result = 0
        for(item in chosen ?: listOf()) {
            if(item.infusion?.active == true) {
                result += 1
            }
        }
        return result
    }

    fun activateInfusion(infusion: Infusion) : Boolean {
        if(this.infusion == infusion) {
            this.infusion.active = true
            return true
        } else {
            this.chosen?.forEachIndexed { index, it ->
                if (it.infusion == infusion) {
                    chosen?.get(index)?.infusion?.active = true
                    return true
                }
            }
        }
        return false
    }

    fun deactivateInfusion(infusion: Infusion): Boolean {
        if(this.infusion == infusion) {
            this.infusion.active = false
            return true
        } else {
            this.chosen?.forEachIndexed { index, it ->
                if (it.infusion == infusion) {
                    chosen?.get(index)?.infusion?.active = false
                    return true
                }
            }
        }
        return false
    }
}