package gmail.loganchazdon.dndhelper.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import gmail.loganchazdon.dndhelper.model.*
import gmail.loganchazdon.dndhelper.model.choiceEntities.RaceChoiceEntity
import gmail.loganchazdon.dndhelper.model.junctionEntities.CharacterRaceCrossRef
import gmail.loganchazdon.dndhelper.model.junctionEntities.RaceFeatureCrossRef


@Database(
    entities = [
        (CharacterEntity::class), (Class::class), (RaceEntity::class), (Feature::class),
        (RaceFeatureCrossRef::class), (CharacterRaceCrossRef::class),
        (RaceChoiceEntity::class)
    ],
    views = [
        (Race::class)
    ],
    version = 58,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class RoomDataBase: RoomDatabase() {
    abstract fun databaseDao(): DatabaseDao
}