package model.database.daos

import kotlinx.coroutines.flow.Flow
import model.Background
import model.BackgroundEntity
import model.Feature
import model.Spell
import model.choiceEntities.BackgroundChoiceEntity

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect abstract class BackgroundDao {
    abstract suspend fun getBackgroundSpells(backgroundId: Int): List<Spell>?
    abstract suspend fun getBackgroundFeatures(id: Int): List<Feature>
    fun getAllBackgrounds(): Flow<List<Background>>
    abstract suspend fun getUnfilledBackgroundFeatures(id: Int): List<Feature>
    abstract fun getHomebrewBackgrounds(): Flow<List<BackgroundEntity>>
    abstract suspend fun deleteBackground(id: Int)
    abstract suspend fun getBackgroundChoiceData(charId: Int): BackgroundChoiceEntity
    suspend fun insertBackgroundFeatureCrossRef(backgroundId: Int, featureId: Int)
    suspend fun insertBackground(backgroundEntity: BackgroundEntity): Int
    suspend fun insertBackgroundSpellCrossRef(backgroundId: Int, spellId: Int)
    abstract fun getUnfilledBackground(id: Int): Flow<BackgroundEntity>
}