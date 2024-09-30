package model.database.daos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.asFlow
import androidx.room.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import model.*

@Dao
actual abstract class SubclassDao {
    @Query(
        """SELECT * FROM subclasses WHERE subclassId IS :id"""
    )
    protected abstract fun getUnfilledSubclass(id: Int): LiveData<SubclassEntityTable>

    @Query(
        """SELECT * FROM features JOIN SubclassFeatureCrossRef ON features.featureId IS SubclassFeatureCrossRef.featureId WHERE SubclassFeatureCrossRef.subclassId IS :id"""
    )
    actual abstract fun getSubclassLiveFeaturesById(id: Int): Flow<List<Feature>>

    actual fun getSubclass(id: Int): Flow<Subclass> {
        val result = MediatorLiveData<Subclass>()

        result.addSource(getUnfilledSubclass(id)) { subclassEntity ->
            if (subclassEntity != null) {
                GlobalScope.launch {
                    val spells: MutableList<Pair<Int, Spell>> = mutableListOf()
                    getSubclassSpells(subclassEntity.subclassId).forEach {
                        spells.add(Pair(it.level, it))
                    }
                    result.postValue(
                        Subclass(subclassEntity, getSubclassFeaturesById(id), spells)
                    )
                }
            }
        }

        return result.asFlow()
    }

    @Query("SELECT * FROM spells JOIN SubclassSpellCrossRef ON SubclassSpellCrossRef.spellId IS spells.id WHERE subclassId IS :id")
    abstract fun getSubclassSpells(id: Int): List<Spell>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertSubclassFeatureCrossRef(subclassFeatureCrossRef: SubclassFeatureCrossRef)
    actual fun insertSubclassFeatureCrossRef(subclassId: Int, featureId: Int) {
        insertSubclassFeatureCrossRef(
            SubclassFeatureCrossRef(
                subclassId = subclassId,
                featureId = featureId
            )
        )
    }

    @Delete
    abstract fun removeSubclassFeatureCrossRef(subclassFeatureCrossRef: SubclassFeatureCrossRef)
    actual fun removeSubclassFeatureCrossRef(subclassId: Int, featureId: Int) {
        removeSubclassFeatureCrossRef(
            SubclassFeatureCrossRef(
                subclassId = subclassId,
                featureId = featureId
            )
        )
    }


    @Query(
        """SELECT * FROM features 
JOIN SubclassFeatureCrossRef ON SubclassFeatureCrossRef.featureId IS features.featureId
WHERE subclassId IS :id"""
    )
    protected abstract fun getSubclassFeaturesById(id: Int): List<Feature>


    @OptIn(DelicateCoroutinesApi::class)
    actual fun getSubclassesByClassId(id: Int): Flow<List<Subclass>> {
        val result = MediatorLiveData<List<Subclass>>()
        result.addSource(getUnfilledSubclassesByClassId(id)) { entities ->
            GlobalScope.launch {
                val tempList = mutableListOf<Subclass>()
                entities.forEach {
                    val spells: MutableList<Pair<Int, Spell>> = mutableListOf()
                    getSubclassSpells(it.subclassId).forEach { spell ->
                        spells.add(Pair(spell.level, spell))
                    }
                    tempList.add(
                        Subclass(
                            subclassEntity = it,
                            features = getSubclassFeaturesById(it.subclassId),
                            spells = spells
                        )
                    )
                }
                result.postValue(tempList)
            }
        }
        return result.asFlow()
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertSubclassSpellCrossRef(ref: SubclassSpellCrossRef)

    @Delete
    abstract fun removeSubclassSpellCrossRef(ref: SubclassSpellCrossRef)


    actual fun insertSubclass(subClass: SubclassEntity): Int {
        val id = insertSubclassOrIgnore(subClass.asTable()).toInt()
        if (id == -1) {
            updateSubclass(subClass.asTable())
            return subClass.subclassId
        }
        return id
    }

    @Update
    protected abstract fun updateSubclass(subClass: SubclassEntityTable)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract fun insertSubclassOrIgnore(subClass: SubclassEntityTable): Long

    @Query(
        """SELECT * FROM subclasses
JOIN ClassSubclassCrossRef ON ClassSubclassCrossRef.subclassId IS subclasses.subclassId
WHERE classId IS :id"""
    )
    protected abstract fun getUnfilledSubclassesByClassId(id: Int): LiveData<List<SubclassEntityTable>>

    @Query("SELECT * FROM subclasses WHERE subclass_isHomebrew IS 1")
    abstract fun getHomebrewSubclassesTable(): Flow<List<SubclassEntityTable>>
    actual fun getHomebrewSubclasses(): Flow<List<SubclassEntity>>  {
        return getHomebrewSubclassesTable()
    }

    @Query(
        """SELECT * FROM features
JOIN SubclassFeatureCrossRef ON SubclassFeatureCrossRef.featureId IS features.featureId
WHERE SubclassFeatureCrossRef.subclassId IS :subclassId AND features.grantedAtLevel <= :maxLevel
    """
    )
    actual abstract fun getSubclassFeatures(subclassId: Int, maxLevel: Int): List<Feature>

    @Query("DELETE FROM subclasses WHERE subclassId = :subclassId")
    actual abstract fun deleteSubclass(subclassId: Int)
}