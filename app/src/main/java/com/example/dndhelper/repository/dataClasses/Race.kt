package com.example.dndhelper.repository.dataClasses

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.dndhelper.repository.model.Converters

@Entity(tableName="races")
@TypeConverters(Converters::class)
data class Race (
    @PrimaryKey(autoGenerate = false)
    @NonNull
    @ColumnInfo(name="name")
    val name : String,
    val groundSpeed: Int,
    val abilityBonuses: List<AbilityBonus>,
    val alignment: String,
    val age : String,
    val size: String,
    val sizeDesc: String,
    val startingProficiencies: List<Proficiency>,
    val languages: List<Language>,
    val languageDesc: String,
    val traits: List<Feature>,
    val subraces: List<Subrace>,
)