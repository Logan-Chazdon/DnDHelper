package com.example.dndhelper.repository.dataClasses

import androidx.annotation.IntegerRes
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.dndhelper.repository.model.Converters

@Entity(tableName="characters")
@TypeConverters(Converters::class)
data class Character(
    @ColumnInfo(name="name")
    var name: String,
    var race: Race? = null
){
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name="id")
    var id: Int = 0

    var baseStats = mutableMapOf<String, Int>()

    var background: Background? = null
}
