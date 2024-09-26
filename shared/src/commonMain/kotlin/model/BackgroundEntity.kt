package model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "backgrounds")
open class BackgroundEntity(
    val name: String,
    val desc: String,
    var spells: List<Spell>?,
    val proficiencies : List<Proficiency>,
    val proficiencyChoices : List<ProficiencyChoice>? = null,
    val languages : List<Language>,
    val languageChoices : List<LanguageChoice>? = null,
    val equipment : List<ItemInterface>,
    val equipmentChoices: List<ItemChoice>,
    var isHomebrew : Boolean = false
) {
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0
}