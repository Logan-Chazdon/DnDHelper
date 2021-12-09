package com.example.dndhelper

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.dndhelper.repository.Repository
import com.example.dndhelper.repository.model.DatabaseDao
import com.example.dndhelper.repository.model.RoomDataBase
import com.example.dndhelper.repository.webServices.Webservice
import com.example.dndhelper.repository.webServices.WebserviceDnD
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

    @Provides
    @ViewModelScoped
    fun providerWebservice(): Webservice {
        return WebserviceDnD()
    }


}
