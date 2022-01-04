package com.example.dndhelper

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Room
import com.example.dndhelper.repository.model.DatabaseDao
import com.example.dndhelper.repository.model.RoomDataBase
import com.example.dndhelper.repository.webServices.LocalDataSource
import com.example.dndhelper.repository.webServices.LocalDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
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
        ).fallbackToDestructiveMigration().build()
    }
}

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule{
    @Provides
    @ViewModelScoped
    fun providerDao(db: RoomDataBase): DatabaseDao? {
        return db.databaseDao()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    @Provides
    @ViewModelScoped
    fun providerWebservice(@ApplicationContext appContext: Context): LocalDataSource {
        return LocalDataSourceImpl(appContext)
    }


}
