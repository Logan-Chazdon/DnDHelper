package gmail.loganchazdon.dndhelper.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class FeatChoiceEntity(
    val name: String,
    val choose : Int,
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0
}