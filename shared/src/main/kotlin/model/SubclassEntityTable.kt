package model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subclasses")
class SubclassEntityTable(
    @ColumnInfo(name = "subclass_name")
    override var name: String,
    spellAreFree: Boolean,
    @ColumnInfo(name = "subclass_spell_casting")
    override var spellCasting: SpellCasting?,
    @ColumnInfo(name = "subclass_isHomebrew")
    override var isHomebrew : Boolean = false
) : SubclassEntity(
    name,
    spellAreFree,
    spellCasting,
    isHomebrew
) {
    @PrimaryKey(autoGenerate = true)
    override var subclassId : Int = 0
}

fun SubclassEntity.asTable() : SubclassEntityTable {
    return SubclassEntityTable(name, spellAreFree, spellCasting,isHomebrew).run {
        subclassId = this@asTable.subclassId; this
    }
}