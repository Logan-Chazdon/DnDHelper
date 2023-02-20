package gmail.loganchazdon.dndhelper

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import gmail.loganchazdon.dndhelper.model.database.RoomDataBase
import gmail.loganchazdon.dndhelper.model.database.daos.*
import gmail.loganchazdon.dndhelper.model.database.migrations.MIGRATION_56_57
import gmail.loganchazdon.dndhelper.model.localDataSources.LocalDataSource
import gmail.loganchazdon.dndhelper.model.localDataSources.LocalDataSourceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): RoomDataBase {
        return Room.databaseBuilder(
            appContext,
            RoomDataBase::class.java,
            "database"
        ).addMigrations(MIGRATION_56_57).build()
    }

    @Provides
    @Singleton
    fun provideLocalDataSource(
        @ApplicationContext appContext: Context,
        backgroundDao: BackgroundDao,
        classDao: ClassDao,
        featDao: FeatDao,
        featureDao: FeatureDao,
        raceDao: RaceDao,
        spellDao: SpellDao,
        subraceDao: SubraceDao,
        subclassDao: SubclassDao
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
            subclassDao = subclassDao
        )
    }


    @Provides
    @Singleton
    fun provideBackgroundDao(database: RoomDataBase): BackgroundDao {
        return database.backgroundDao()
    }

    @Provides
    @Singleton
    fun provideCharacterDao(database: RoomDataBase): CharacterDao {
        return database.characterDao()
    }

    @Provides
    @Singleton
    fun provideClassDao(database: RoomDataBase): ClassDao {
        return database.classDao()
    }

    @Provides
    @Singleton
    fun provideFeatDao(database: RoomDataBase): FeatDao {
        return database.featDao()
    }

    @Provides
    @Singleton
    fun provideFeatureDao(database: RoomDataBase): FeatureDao {
        return database.featureDao()
    }

    @Provides
    @Singleton
    fun provideRaceDao(database: RoomDataBase): RaceDao {
        return database.raceDao()
    }

    @Provides
    @Singleton
    fun provideSpellDao(database: RoomDataBase): SpellDao {
        return database.spellDao()
    }

    @Provides
    @Singleton
    fun provideSubclassDao(database: RoomDataBase): SubclassDao {
        return database.subclassDao()
    }

    @Provides
    @Singleton
    fun provideSubraceDao(database: RoomDataBase): SubraceDao {
        return database.subraceDao()
    }
}
