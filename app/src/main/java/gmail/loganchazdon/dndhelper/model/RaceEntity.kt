package gmail.loganchazdon.dndhelper.model

import androidx.room.*
import gmail.loganchazdon.dndhelper.model.database.Converters


@Entity(tableName="races")
@TypeConverters(Converters::class)
open class RaceEntity (
    @PrimaryKey(autoGenerate = true)
    open var id: Int = 0,
    open var name : String = "",
    open var groundSpeed: Int = 30,
    open var abilityBonuses: List<AbilityBonus>? = null,
    open var abilityBonusChoice: AbilityBonusChoice? = null,
    open var alignment: String? = null,
    open var age : String = "",
    open var size: String = "Medium",
    open var sizeDesc: String = "",
    open var startingProficiencies: List<Proficiency> = listOf(),
    open var proficiencyChoices : List<ProficiencyChoice> = listOf(),
    open var languages: List<Language> = listOf(),
    open var languageChoices: List<LanguageChoice> = listOf(),
    open var languageDesc: String = "",
    //TODO remove this after updating LocalDataSource.
    open var subraces: List<Subrace>? = listOf()
)
