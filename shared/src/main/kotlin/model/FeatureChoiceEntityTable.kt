package model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "FeatureChoiceEntity" )
class FeatureChoiceEntityTable(
    choose: Choose = Choose(0),
) : FeatureChoiceEntity(
    choose
){
    @PrimaryKey(autoGenerate = true)
    override var id: Int = 0
}

fun FeatureChoiceEntity.asTable(): FeatureChoiceEntityTable {
    return FeatureChoiceEntityTable(choose).run {
        id = this@asTable.id; this
    }
}