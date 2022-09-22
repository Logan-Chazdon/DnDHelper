package gmail.loganchazdon.dndhelper.model

import androidx.room.*
import gmail.loganchazdon.dndhelper.model.database.Converters

@Entity(tableName="features")
@TypeConverters(Converters::class)
data class Feature(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true)
    var featureId: Int = 0,
    val name: String,
    val description: String,
    val index : String? = null, //This is used when we need to check for a specific feature. For example when a subrace overrides a race.
    val grantedAtLevel: Int = 1,
    val maxTimesChosen : Int? = null, //If the feature is in another features options. This is the max amount of times the user can select it.
    val prerequisite: Prerequisite? = null,
    val activationRequirement: ActivationRequirement = ActivationRequirement(),
    val speedBoost: ScalingBonus? = null,
    val spells: List<Spell>? = null, //Spells granted by this feature
    val infusion: Infusion? = null,
    val maxActive: Choose = Choose(0),
    val hpBonusPerLevel: Int? = null,
    val armorContingentAcBonus: Int? = null, //Extra ac only granted when wearing armor.
    val acBonus: Int? = null,
    val ac: ArmorClass? = null, //This will only be applied when not wearing armor.
    val proficiencies: List<Proficiency>? = null,
    val expertises: List<Proficiency>? = null,
    val languages: List<Language>? = null,
    val extraAttackAndDamageRollStat: String? = null, //This adds an additional stat to the stats you can use when rolling attack or damage.
    val rangedAttackBonus: Int? = null, //Number added to all ranged attack roles.
    val choices: List<FeatureChoice>? = null
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

    fun activateInfusion(infusion: Infusion) : Boolean {
        if(this.infusion == infusion) {
            this.infusion.active = true
            return true
        } else {
            allChosen.forEach {
                if (it.infusion == infusion) {
                    it.infusion.active = true
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
            allChosen.forEach {
                if (it.infusion == infusion) {
                    it.infusion.active = false
                    return true
                }
            }
        }
        return false
    }
}