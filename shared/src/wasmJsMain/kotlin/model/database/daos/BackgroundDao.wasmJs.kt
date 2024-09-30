package model.database.daos

import kotlinx.coroutines.flow.Flow
import model.Background
import model.BackgroundEntity
import model.Feature
import model.Spell
import model.choiceEntities.BackgroundChoiceEntity

actual abstract class BackgroundDao {
    actual abstract fun getBackgroundSpells(backgroundId: Int): List<Spell>?
    actual abstract fun removeBackgroundById(id: Int)
    actual abstract fun getBackgroundFeatures(id: Int): List<Feature>
    actual fun getAllBackgrounds(): Flow<List<Background>> {
        TODO("Not yet implemented")
    }

    actual abstract fun getUnfilledBackgroundFeatures(id: Int): List<Feature>
    actual abstract fun getHomebrewBackgrounds(): Flow<List<BackgroundEntity>>
    actual abstract fun deleteBackground(id: Int)
    actual abstract fun getBackgroundChoiceData(charId: Int): BackgroundChoiceEntity
    actual fun insertBackgroundFeatureCrossRef(backgroundId: Int, featureId: Int) {
    }

    actual fun insertBackground(backgroundEntity: BackgroundEntity): Int {
        TODO("Not yet implemented")
    }

    actual fun insertBackgroundSpellCrossRef(backgroundId: Int, spellId: Int) {
    }

    actual abstract fun getUnfilledBackground(id: Int): Flow<BackgroundEntity>
}



class BackgroundDaoImpl() : BackgroundDao() {
    override fun getBackgroundSpells(backgroundId: Int): List<Spell>? {
        TODO("Not yet implemented")
    }

    override fun removeBackgroundById(id: Int) {
        TODO("Not yet implemented")
    }

    override fun getBackgroundFeatures(id: Int): List<Feature> {
        TODO("Not yet implemented")
    }

    override fun getUnfilledBackgroundFeatures(id: Int): List<Feature> {
        TODO("Not yet implemented")
    }

    override fun getHomebrewBackgrounds(): Flow<List<BackgroundEntity>> {
        TODO("Not yet implemented")
    }

    override fun deleteBackground(id: Int) {
        TODO("Not yet implemented")
    }

    override fun getBackgroundChoiceData(charId: Int): BackgroundChoiceEntity {
        TODO("Not yet implemented")
    }

    override fun getUnfilledBackground(id: Int): Flow<BackgroundEntity> {
        TODO("Not yet implemented")
    }

}