package gmail.loganchazdon.dndhelper.model.database.daos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.room.*
import gmail.loganchazdon.dndhelper.model.Background
import gmail.loganchazdon.dndhelper.model.BackgroundEntity
import gmail.loganchazdon.dndhelper.model.Feature
import gmail.loganchazdon.dndhelper.model.Spell
import gmail.loganchazdon.dndhelper.model.choiceEntities.BackgroundChoiceEntity
import gmail.loganchazdon.dndhelper.model.junctionEntities.BackgroundFeatureCrossRef
import gmail.loganchazdon.dndhelper.model.junctionEntities.BackgroundSpellCrossRef
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Dao
abstract class BackgroundDao {
    @Query(
        """SELECT * FROM spells 
JOIN BackgroundSpellCrossRef ON BackgroundSpellCrossRef.spellId IS spells.id
WHERE backgroundId IS :backgroundId
    """
    )
    abstract fun getBackgroundSpells(backgroundId: Int): List<Spell>?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract fun insertBackgroundOrIgnore(backgroundEntity: BackgroundEntity) : Long

    @Update
    protected abstract fun updateBackground(backgroundEntity: BackgroundEntity)

    fun insertBackground(backgroundEntity: BackgroundEntity): Int {
        val id = insertBackgroundOrIgnore(backgroundEntity).toInt()
        if(id == -1) {
            updateBackground(backgroundEntity)
            return backgroundEntity.id
        }
        return id
    }

    @Query("DELETE FROM backgrounds WHERE id = :id")
    abstract fun removeBackgroundById(id: Int)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertBackgroundFeatureCrossRef(ref: BackgroundFeatureCrossRef)

    @Query("SELECT * FROM BackgroundChoiceEntity WHERE characterId IS :charId")
    abstract fun getBackgroundChoiceData(charId: Int): BackgroundChoiceEntity

    @Query(
        """SELECT * FROM features 
JOIN BackgroundFeatureCrossRef ON features.featureId IS BackgroundFeatureCrossRef.featureId
WHERE backgroundId IS :id
    """
    )
    abstract fun getBackgroundFeatures(id: Int): List<Feature>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertBackgroundSpellCrossRef(ref: BackgroundSpellCrossRef)

    @Delete
    abstract fun removeBackgroundSpellCrossRef(ref: BackgroundSpellCrossRef)


    @Query("SELECT * FROM backgrounds")
    protected abstract fun getUnfilledBackgrounds(): LiveData<List<BackgroundEntity>>

    fun getAllBackgrounds(): LiveData<List<Background>> {
        val result = MediatorLiveData<List<Background>>()
        result.addSource(getUnfilledBackgrounds()) { backgroundEntities ->
            GlobalScope.launch {
                val backgrounds = mutableListOf<Background>()
                backgroundEntities.forEach {
                    backgrounds.add(
                        Background(
                            it,
                            getBackgroundFeatures(it.id)
                        )
                    )
                }
                result.postValue(backgrounds)
            }
        }
        return result
    }

    @Query("SELECT * FROM backgrounds WHERE id IS :id")
    abstract fun getUnfilledBackground(id: Int): LiveData<BackgroundEntity>


    @Query(
        """SELECT * FROM features
JOIN BackgroundFeatureCrossRef ON BackgroundFeatureCrossRef.featureId IS features.featureId 
WHERE backgroundId IS :id"""
    )
    abstract fun getUnfilledBackgroundFeatures(id: Int): List<Feature>

    @Query("SELECT * FROM backgrounds WHERE isHomebrew = 1")
    abstract fun getHomebrewBackgrounds(): LiveData<List<BackgroundEntity>>
    @Query("DELETE FROM backgrounds WHERE id = :id")
    abstract fun deleteBackground(id: Int)
}