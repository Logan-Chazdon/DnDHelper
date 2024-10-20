package gmail.loganchazdon.dndhelper.model


class Feature(
    featureId: Int = 0,
    name: String,
    description: String,
    index: String? = null, //This is used when we need to check for a specific feature. For example when a subrace overrides a race.
    grantedAtLevel: Int = 1,
    maxTimesChosen: Int? = null, //If the feature is in another features options. This is the max amount of times the user can select it.
    prerequisite: Prerequisite? = null,
    activationRequirement: ActivationRequirement = ActivationRequirement(),
    speedBoost: ScalingBonus? = null,
    spells: List<Spell>? = null, //Spells granted by this feature
    infusion: Infusion? = null,
    maxActive: Choose = Choose(0),
    hpBonusPerLevel: Int? = null,
    armorContingentAcBonus: Int? = null, //Extra ac only granted when wearing armor.
    acBonus: Int? = null,
    ac: ArmorClass? = null, //This will only be applied when not wearing armor.
    proficiencies: List<Proficiency>? = null,
    expertises: List<Proficiency>? = null,
    languages: List<Language>? = null,
    extraAttackAndDamageRollStat: String? = null, //This adds an additional stat to the stats you can use when rolling attack or damage.
    rangedAttackBonus: Int? = null, //Number added to all ranged attack roles.
    var choices: List<FeatureChoice>? = null
) : FeatureEntity(
    featureId,
    name,
    description,
    index,
    grantedAtLevel,
    maxTimesChosen,
    prerequisite,
    activationRequirement,
    speedBoost,
    spells,
    infusion,
    maxActive,
    hpBonusPerLevel,
    armorContingentAcBonus,
    acBonus,
    ac,
    proficiencies,
    expertises,
    languages,
    extraAttackAndDamageRollStat,
    rangedAttackBonus,
) {
    var resource: Resource? = null
    val allChosen: List<Feature>
        get() {
            val result = mutableListOf<Feature>()
            choices?.forEach {
                it.chosen?.let { chosen -> result.addAll(chosen) }
            }
            return result
        }

    val grantsInfusions: Boolean
        get() {
            if (infusion != null) {
                return true
            }
            allChosen.forEach {
                if (it.grantsInfusions) {
                    return true
                }
            }
            return false
        }

    fun recharge(basis: Int) {
        resource?.recharge(basis)
        allChosen.forEach {
            it.resource?.recharge(basis)
        }
    }

    fun getAvailableOptionsAt(
        index: Int,
        character: Character?,
        assumedProficiencies: List<Proficiency>,
        assumedFeatures: List<Feature>,
        level: Int,
        assumedClass: Class?,
        assumedSpells: List<Spell>,
        assumedStatBonuses: Map<String, Int>?
    ): MutableList<Feature> {
        val result = mutableListOf<Feature>()

        choices?.get(index)?.options?.forEach {
            if (
                try {
                    level >= it.grantedAtLevel
                } catch (e: NumberFormatException) {
                    false
                } &&
                it.prerequisite?.check(
                    character,
                    assumedFeatures,
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
        allChosen.forEach {
            it.spells?.let { spells -> result.addAll(spells) }
        }
        return result
    }

    val currentActive: Int
        get() {
            var result = 0
            for (item in allChosen) {
                if (item.infusion?.active == true) {
                    result += 1
                }
            }
            return result
        }


    fun activateInfusion(infusion: Infusion): Boolean {
        if (this.infusion == infusion) {
            this.infusion!!.active = true
            return true
        } else {
            allChosen.forEach {
                if (it.infusion == infusion) {
                    it.infusion!!.active = true
                    return true
                }
            }
        }
        return false
    }

    fun deactivateInfusion(infusion: Infusion): Boolean {
        if (this.infusion == infusion) {
            this.infusion!!.active = false
            return true
        } else {
            allChosen.forEach {
                if (it.infusion == infusion) {
                    it.infusion!!.active = false
                    return true
                }
            }
        }
        return false
    }

    //This isn't ideal but it cant be a data class or else gson freaks out.
    //TODO look for a  better solution.
    fun copy(): Feature {
        return Feature(
            featureId,
            name,
            description,
            index,
            grantedAtLevel,
            maxTimesChosen,
            prerequisite,
            activationRequirement,
            speedBoost,
            spells,
            infusion,
            maxActive,
            hpBonusPerLevel,
            armorContingentAcBonus,
            acBonus,
            ac,
            proficiencies,
            expertises,
            languages,
            extraAttackAndDamageRollStat,
            rangedAttackBonus,
            choices
        )
    }
}