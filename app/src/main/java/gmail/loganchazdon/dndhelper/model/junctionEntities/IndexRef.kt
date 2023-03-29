package gmail.loganchazdon.dndhelper.model.junctionEntities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class IndexRef(
    @PrimaryKey
    val index: String,
    val ids : List<Int>
)
