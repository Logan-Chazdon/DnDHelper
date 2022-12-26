package gmail.loganchazdon.dndhelper.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "backgrounds")
open class BackgroundEntity(
    val name: String,
    val desc: String,
    var spells: List<Spell>?, //TODO extract me when spells get a table
    val proficiencies : List<Proficiency>,
    val languages : List<Language>,
    val equipment : List<ItemInterface>,
    val equipmentChoices: List<ItemChoice>, //TODO extract me to a table when items get a table
    var isHomebrew : Boolean = false
) {
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0
}