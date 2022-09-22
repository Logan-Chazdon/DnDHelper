package gmail.loganchazdon.dndhelper.model.database

import androidx.room.*
import gmail.loganchazdon.dndhelper.model.*
import gmail.loganchazdon.dndhelper.model.junctionEntities.RaceFeatureCrossRef


@Database(
    entities = [(Character::class), (Class::class), (RaceEntity::class), (Feature::class), (RaceFeatureCrossRef::class)],
    version = 58,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class RoomDataBase: RoomDatabase() {
    abstract fun databaseDao(): DatabaseDao
}