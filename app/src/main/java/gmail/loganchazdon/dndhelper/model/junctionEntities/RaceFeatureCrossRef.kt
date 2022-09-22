package gmail.loganchazdon.dndhelper.model.junctionEntities

import androidx.room.Entity
import androidx.room.TypeConverters
import gmail.loganchazdon.dndhelper.model.database.Converters

@TypeConverters(Converters::class)
@Entity(primaryKeys = ["id", "featureId"])
data class RaceFeatureCrossRef(
    val featureId: Int,
    val id: Int
)