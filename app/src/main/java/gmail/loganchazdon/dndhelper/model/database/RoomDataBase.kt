package gmail.loganchazdon.dndhelper.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import gmail.loganchazdon.dndhelper.model.Character
import gmail.loganchazdon.dndhelper.model.Class
import gmail.loganchazdon.dndhelper.model.Race


@Database(
    entities = [(Character::class), (Class::class), (Race::class)],
    version = 57,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class RoomDataBase: RoomDatabase() {
    abstract fun databaseDao(): DatabaseDao
}