package gmail.loganchazdon.dndhelper.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import gmail.loganchazdon.dndhelper.model.Character
import gmail.loganchazdon.dndhelper.model.Class
import gmail.loganchazdon.dndhelper.model.Race


@Database(entities = [(Character::class), (Class::class), (Race::class)], version = 56)
@TypeConverters(Converters::class)
abstract class RoomDataBase: RoomDatabase() {
    abstract fun databaseDao(): DatabaseDao

    companion object  {
        private var INSTANCE: RoomDataBase? = null
        internal fun getDatabase(context: Context) : RoomDataBase? {
            if(INSTANCE == null) {
                synchronized(RoomDataBase::class.java) {
                    INSTANCE = Room.databaseBuilder<RoomDataBase>(
                        context.applicationContext,
                        RoomDataBase::class.java,
                        "database"
                    ).fallbackToDestructiveMigration()
                        .build()

                }
            }

            return INSTANCE
        }

    }}