package gmail.loganchazdon.dndhelper.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import gmail.loganchazdon.dndhelper.model.*
import gmail.loganchazdon.dndhelper.model.choiceEntities.FeatureChoiceChoiceEntity
import gmail.loganchazdon.dndhelper.model.choiceEntities.RaceChoiceEntity
import gmail.loganchazdon.dndhelper.model.junctionEntities.*


@Database(
    entities = [
        (CharacterEntity::class), (ClassEntity::class), (RaceEntity::class), (FeatureEntity::class), (FeatureChoiceEntity::class),
        (RaceFeatureCrossRef::class), (CharacterRaceCrossRef::class), (FeatureOptionsCrossRef::class),
        (OptionsFeatureCrossRef::class), (ClassFeatureCrossRef::class),
        (RaceChoiceEntity::class), (FeatureChoiceChoiceEntity::class),
    ],
    version = 58,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class RoomDataBase: RoomDatabase() {
    abstract fun databaseDao(): DatabaseDao
}