package model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import model.converters.Converters


@Entity(tableName="features")
@TypeConverters(Converters::class)
open class FeatureEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true)
    var featureId: Int = 0,
    var name: String,
    var description: String,
    var index : String? = null, //This is used when we need to check for a specific feature. For example when a subrace overrides a race.
    var grantedAtLevel: Int = 1,
    var maxTimesChosen : Int? = null, //If the feature is in another features options. This is the max amount of times the user can select it.
    var prerequisite: Prerequisite? = null,
    var activationRequirement: ActivationRequirement = ActivationRequirement(),
    var speedBoost: ScalingBonus? = null,
    var spells: List<Spell>? = null, //Spells granted by this feature
    var infusion: Infusion? = null,
    var maxActive: Choose = Choose(0),
    var hpBonusPerLevel: Int? = null,
    var armorContingentAcBonus: Int? = null, //Extra ac only granted when wearing armor.
    var acBonus: Int? = null,
    var ac: ArmorClass? = null, //This will only be applied when not wearing armor.
    var proficiencies: List<Proficiency>? = null,
    var expertises: List<Proficiency>? = null,
    var languages: List<Language>? = null,
    var extraAttackAndDamageRollStat: String? = null, //This adds an additional stat to the stats you can use when rolling attack or damage.
    var rangedAttackBonus: Int? = null, //Number added to all ranged attack roles.
)