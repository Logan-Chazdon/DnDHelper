package model.database.daos

import kotlinx.coroutines.flow.Flow
import model.Background
import model.BackgroundEntity
import model.Feature
import model.Spell
import model.choiceEntities.BackgroundChoiceEntity

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect abstract class BackgroundDao {
    abstract fun getBackgroundSpells(backgroundId: Int): List<Spell>?
    abstract fun removeBackgroundById(id: Int)
    abstract fun getBackgroundFeatures(id: Int): List<Feature>
    fun getAllBackgrounds(): Flow<List<Background>>
    abstract fun getUnfilledBackgroundFeatures(id: Int): List<Feature>
    abstract fun getHomebrewBackgrounds(): Flow<List<BackgroundEntity>>
    abstract fun deleteBackground(id: Int)
    abstract fun getBackgroundChoiceData(charId: Int): BackgroundChoiceEntity
    fun insertBackgroundFeatureCrossRef(backgroundId: Int, featureId: Int)
    fun insertBackground(backgroundEntity: BackgroundEntity): Int
    fun insertBackgroundSpellCrossRef(backgroundId: Int, spellId: Int)
    abstract fun getUnfilledBackground(id: Int): Flow<BackgroundEntity>
}