package model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "spells")
class SpellTable(
    name: String, level: Int, components: List<String>, itemComponents: List<ItemInterface>,
    school: String, desc: String, range: String, area: String, castingTime: String, duration: String,
    classes: List<String>, damage: String
) : Spell(
    name, level, components,
    itemComponents, school, desc, range, area, castingTime, duration, classes, damage
) {
    @PrimaryKey(autoGenerate = true)
    override var id = 0
}

fun Spell.asTable(): SpellTable {
    return SpellTable(
        name, level, components,
        itemComponents, school, desc, range, area, castingTime, duration, classes, damage
    ).run { id = this@asTable.id; this}
}