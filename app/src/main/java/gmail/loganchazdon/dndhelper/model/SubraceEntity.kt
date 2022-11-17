package gmail.loganchazdon.dndhelper.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subraces")
open class SubraceEntity(
    var name : String,
    var abilityBonuses: List<AbilityBonus>?,
    var abilityBonusChoice: AbilityBonusChoice?,
    var startingProficiencies: List<Proficiency>?,
    var languages : List<Language>,
    var languageChoices: List<LanguageChoice>,
    var size: String?,
    var groundSpeed: Int?,
)  {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}