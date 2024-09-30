package model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "backgrounds")
class BackgroundEntityTable(
    name: String,
    desc: String,
    spells: List<Spell>?,
    proficiencies: List<Proficiency>,
    proficiencyChoices: List<ProficiencyChoice>? = null,
    languages: List<Language>,
    languageChoices: List<LanguageChoice>? = null,
    equipment: List<ItemInterface>,
    equipmentChoices: List<ItemChoice>,
    isHomebrew: Boolean = false
) : BackgroundEntity(
    name,
    desc,
    spells,
    proficiencies,
    proficiencyChoices,
    languages,
    languageChoices,
    equipment,
    equipmentChoices,
    isHomebrew
) {
    @PrimaryKey(autoGenerate = true)
    override var id: Int = 0
}

fun BackgroundEntity.asTable(): BackgroundEntityTable {
    return BackgroundEntityTable(
        name,
        desc,
        spells,
        proficiencies,
        proficiencyChoices,
        languages,
        languageChoices,
        equipment,
        equipmentChoices,
        isHomebrew
    ).run { id = this@asTable.id; this}
}