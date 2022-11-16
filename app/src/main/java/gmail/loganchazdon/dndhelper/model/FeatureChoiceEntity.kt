package gmail.loganchazdon.dndhelper.model

import androidx.room.Entity
import androidx.room.PrimaryKey



@Entity
open class FeatureChoiceEntity(
    open val choose: Choose = Choose(0),
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}