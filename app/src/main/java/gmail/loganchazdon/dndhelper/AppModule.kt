package gmail.loganchazdon.dndhelper

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import gmail.loganchazdon.dndhelper.model.database.DatabaseDao
import gmail.loganchazdon.dndhelper.model.database.MIGRATION_56_57
import gmail.loganchazdon.dndhelper.model.database.RoomDataBase
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
    fun providerLocalDataSource(@ApplicationContext appContext: Context, dao: DatabaseDao): LocalDataSource {
        return LocalDataSourceImpl(appContext, dao)
    }

    @Provides
    fun providerDao(db: RoomDataBase): DatabaseDao {
        return db.databaseDao()
    }
}

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule{

}
