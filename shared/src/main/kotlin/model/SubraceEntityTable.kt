package model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subraces")
class SubraceEntityTable(
    name: String,
    abilityBonuses: List<AbilityBonus>? = null,
    abilityBonusChoice: AbilityBonusChoice? = null,
    startingProficiencies: List<Proficiency>? = null,
    languages: List<Language> = emptyList(),
    languageChoices: List<LanguageChoice> = emptyList(),
    size: String? = "Medium",
    groundSpeed: Int? = null,
    isHomebrew: Boolean = false,
) : SubraceEntity(
    name,
    abilityBonuses,
    abilityBonusChoice,
    startingProficiencies,
    languages,
    languageChoices,
    size,
    groundSpeed,
    isHomebrew
) {
    @PrimaryKey(autoGenerate = true)
    override var id: Int = 0
}

fun SubraceEntity.asTable() : SubraceEntityTable {
    return SubraceEntityTable(
        name,
        abilityBonuses,
        abilityBonusChoice,
        startingProficiencies,
        languages,
        languageChoices,
        size,
        groundSpeed,
        isHomebrew
    ).run { id = this@asTable.id; this }
}