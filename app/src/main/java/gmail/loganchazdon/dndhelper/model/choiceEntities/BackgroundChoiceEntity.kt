package gmail.loganchazdon.dndhelper.model.choiceEntities

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
data class BackgroundChoiceEntity(
    @PrimaryKey
    val characterId: Int,
)
