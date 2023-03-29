package gmail.loganchazdon.dndhelper.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subclasses")
open class SubclassEntity(
    @ColumnInfo(name = "subclass_name")
    var name: String,
    val spellAreFree: Boolean,
    @ColumnInfo(name = "subclass_spell_casting")
    var spellCasting: SpellCasting?,
    @ColumnInfo(name = "subclass_isHomebrew")
    var isHomebrew : Boolean = false
) {
    @PrimaryKey(autoGenerate = true)
    var subclassId : Int = 0
}
