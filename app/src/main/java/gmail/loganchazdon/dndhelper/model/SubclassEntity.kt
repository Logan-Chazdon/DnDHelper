package gmail.loganchazdon.dndhelper.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subclasses")
open class SubclassEntity (
    var name: String,
    var spells: List<Pair<Int, Spell>>?,
    val spellAreFree: Boolean,
    var spellCasting: SpellCasting?
) {
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0
}
