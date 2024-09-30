package model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "featChoices")
class FeatChoiceEntityTable(
    name: String,
    choose : Int,
) : FeatChoiceEntity(
    name,
    choose
) {
    @PrimaryKey(autoGenerate = true)
    override var id = 0
}

fun FeatChoiceEntity.asTable() : FeatChoiceEntityTable {
    return FeatChoiceEntityTable(name, choose).run {
        id = this@asTable.id; this
    }
}