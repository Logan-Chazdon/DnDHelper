package gmail.loganchazdon.dndhelper.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import gmail.loganchazdon.dndhelper.model.database.Converters


@Entity(tableName="races")
@TypeConverters(Converters::class)
open class RaceEntity (
    @PrimaryKey(autoGenerate = true)
    open var raceId: Int = 0,
    open var raceName : String = "",
    open var groundSpeed: Int = 30,
    open var abilityBonuses: List<AbilityBonus>? = null,
    @Embedded(prefix = "abc")
    open var abilityBonusChoice: AbilityBonusChoice? = null,
    open var alignment: String? = null,
    open var age : String = "",
    open var size: String = "Medium",
    open var sizeDesc: String = "",
    open var startingProficiencies: List<Proficiency> = listOf(),
    open var proficiencyChoices : List<ProficiencyChoice> = ArrayList(),
    open var languages: List<Language> = listOf(),
    open var languageChoices: List<LanguageChoice> = listOf(),
    open var languageDesc: String = "",
    var isHomebrew : Boolean = false
)
