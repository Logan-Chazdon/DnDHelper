package model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import model.database.converters.Converters


@Entity(tableName = "features")
@TypeConverters(Converters::class)
class FeatureEntityTable(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true)
    override var featureId: Int = 0,
    name: String,
    description: String,
    index: String? = null,
    grantedAtLevel: Int = 1,
    maxTimesChosen: Int? = null,
    prerequisite: Prerequisite? = null,
    activationRequirement: ActivationRequirement = ActivationRequirement(),
    speedBoost: ScalingBonus? = null,
    spells: List<Spell>? = null,
    infusion: Infusion? = null,
    maxActive: Choose = Choose(0),
    hpBonusPerLevel: Int? = null,
    armorContingentAcBonus: Int? = null,
    acBonus: Int? = null,
    ac: ArmorClass? = null,
    proficiencies: List<Proficiency>? = null,
    expertises: List<Proficiency>? = null,
    languages: List<Language>? = null,
    extraAttackAndDamageRollStat: String? = null,
    rangedAttackBonus: Int? = null,
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
    rangedAttackBonus
)

fun FeatureEntity.asTable(): FeatureEntityTable {
    return FeatureEntityTable(
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
        rangedAttackBonus
    )
}