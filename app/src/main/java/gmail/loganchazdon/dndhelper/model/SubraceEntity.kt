package gmail.loganchazdon.dndhelper.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subraces")
open class SubraceEntity(
    var name : String,
    var abilityBonuses: List<AbilityBonus>? = null,
    var abilityBonusChoice: AbilityBonusChoice?= null,
    var startingProficiencies: List<Proficiency>?= null,
    var languages : List<Language> = emptyList(),
    var languageChoices: List<LanguageChoice> = emptyList(),
    var size: String? = "Medium",
    var groundSpeed: Int? = null,
    var isHomebrew : Boolean = false,
)  {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}