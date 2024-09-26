package gmail.loganchazdon.dndhelper

import android.content.Context
import androidx.room.Room
import model.database.RoomDataBase
import model.database.daos.*
import model.database.migrations.MIGRATION_56_57
import model.database.migrations.MIGRATION_57_58
import model.localDataSources.LocalDataSource
import model.localDataSources.LocalDataSourceImpl
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import org.koin.dsl.module


@Module
class AppModule {
    val module = module {
        @Single
        fun provideAppDatabase(appContext: Context): RoomDataBase {
            return Room.databaseBuilder(
                appContext,
                RoomDataBase::class.java,
                "database"
            ).addMigrations(MIGRATION_56_57, MIGRATION_57_58).fallbackToDestructiveMigration().build()
        }


        @Single
        fun provideLocalDataSource(
            appContext: Context,
            backgroundDao: BackgroundDao,
            classDao: ClassDao,
            featDao: FeatDao,
            featureDao: FeatureDao,
            raceDao: RaceDao,
            spellDao: SpellDao,
            subraceDao: SubraceDao,
            subclassDao: SubclassDao,
            database: RoomDataBase
        ): LocalDataSource {
            return LocalDataSourceImpl(
                context = appContext,
                backgroundDao = backgroundDao,
                classDao = classDao,
                featDao = featDao,
                raceDao = raceDao,
                spellDao = spellDao,
                featureDao = featureDao,
                subraceDao = subraceDao,
                subclassDao = subclassDao,
                db = database
            )
        }


        @Single
        fun provideBackgroundDao(database: RoomDataBase): BackgroundDao {
            return database.backgroundDao()
        }


        @Single
        fun provideCharacterDao(database: RoomDataBase): CharacterDao {
            return database.characterDao()
        }


        @Single
        fun provideClassDao(database: RoomDataBase): ClassDao {
            return database.classDao()
        }


        @Single
        fun provideFeatDao(database: RoomDataBase): FeatDao {
            return database.featDao()
        }


        @Single
        fun provideFeatureDao(database: RoomDataBase): FeatureDao {
            return database.featureDao()
        }


        @Single
        fun provideRaceDao(database: RoomDataBase): RaceDao {
            return database.raceDao()
        }

        @Single
        fun provideSpellDao(database: RoomDataBase): SpellDao {
            return database.spellDao()
        }


        @Single
        fun provideSubclassDao(database: RoomDataBase): SubclassDao {
            return database.subclassDao()
        }


        @Single
        fun provideSubraceDao(database: RoomDataBase): SubraceDao {
            return database.subraceDao()
        }
    }
}
