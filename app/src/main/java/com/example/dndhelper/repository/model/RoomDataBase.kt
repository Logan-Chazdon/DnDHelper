package com.example.dndhelper.repository.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.dndhelper.repository.dataClasses.Character
import com.example.dndhelper.repository.dataClasses.Class
import com.example.dndhelper.repository.dataClasses.Race



@Database(entities = [(Character::class), (Class::class), (Race::class)], version = 55)
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
                    ).fallbackToDestructiveMigration()  //TODO update this after release to avoid getting stabbed
                        .build()

                }
            }

            return INSTANCE
        }

    }}