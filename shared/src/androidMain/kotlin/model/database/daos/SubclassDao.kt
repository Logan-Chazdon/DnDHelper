package model.database.daos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.room.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import model.Feature
import model.Spell
import model.Subclass
import model.SubclassEntity
import model.junctionEntities.SubclassFeatureCrossRef
import model.junctionEntities.SubclassSpellCrossRef

@Dao
abstract class SubclassDao {
    @Query(
        """SELECT * FROM subclasses WHERE subclassId IS :id"""
    )
    protected abstract fun getUnfilledSubclass(id: Int): LiveData<SubclassEntity>

    @Query(
        """SELECT * FROM features JOIN SubclassFeatureCrossRef ON features.featureId IS SubclassFeatureCrossRef.featureId WHERE SubclassFeatureCrossRef.subclassId IS :id"""
    )
    abstract fun getSubclassLiveFeaturesById(id: Int): LiveData<List<Feature>>

    fun getSubclass(id: Int): LiveData<Subclass> {
        val result = MediatorLiveData<Subclass>()

        result.addSource(getUnfilledSubclass(id)) { subclassEntity ->
            if (subclassEntity != null) {
                GlobalScope.launch {
                    val spells : MutableList<Pair<Int, Spell>> = mutableListOf()
                    getSubclassSpells(subclassEntity.subclassId).forEach {
                        spells.add(Pair(it.level, it))
                    }
                    result.postValue(
                        Subclass(subclassEntity, getSubclassFeaturesById(id), spells)
                    )
                }
            }
        }

        return result
    }

    @Query("SELECT * FROM spells JOIN SubclassSpellCrossRef ON SubclassSpellCrossRef.spellId IS spells.id WHERE subclassId IS :id")
    abstract fun getSubclassSpells(id: Int) : List<Spell>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertSubclassFeatureCrossRef(subclassFeatureCrossRef: SubclassFeatureCrossRef)

    @Delete
    abstract fun removeSubclassFeatureCrossRef(subclassFeatureCrossRef: SubclassFeatureCrossRef)


    @Query(
        """SELECT * FROM features 
JOIN SubclassFeatureCrossRef ON SubclassFeatureCrossRef.featureId IS features.featureId
WHERE subclassId IS :id"""
    )
    protected abstract fun getSubclassFeaturesById(id: Int): List<Feature>



    @OptIn(DelicateCoroutinesApi::class)
    fun getSubclassesByClassId(id: Int): LiveData<List<Subclass>> {
        val result = MediatorLiveData<List<Subclass>>()
        result.addSource(getUnfilledSubclassesByClassId(id)) { entities ->
            GlobalScope.launch {
                val tempList = mutableListOf<Subclass>()
                entities.forEach {
                    val spells : MutableList<Pair<Int, Spell>> = mutableListOf()
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
        return result
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertSubclassSpellCrossRef(ref: SubclassSpellCrossRef)

    @Delete
    abstract fun removeSubclassSpellCrossRef(ref: SubclassSpellCrossRef)


    fun insertSubclass(subClass: SubclassEntity): Int {
        val id = insertSubclassOrIgnore(subClass).toInt()
        if(id == -1) {
            updateSubclass(subClass)
            return subClass.subclassId
        }
        return id
    }

    @Update
    protected abstract fun updateSubclass(subClass: SubclassEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract fun insertSubclassOrIgnore(subClass: SubclassEntity): Long

    @Query(
        """SELECT * FROM subclasses
JOIN ClassSubclassCrossRef ON ClassSubclassCrossRef.subclassId IS subclasses.subclassId
WHERE classId IS :id"""
    )
    protected abstract fun getUnfilledSubclassesByClassId(id: Int): LiveData<List<SubclassEntity>>
    @Query("SELECT * FROM subclasses WHERE subclass_isHomebrew IS 1")
    abstract fun getHomebrewSubclasses(): LiveData<List<SubclassEntity>>

    @Query(
        """SELECT * FROM features
JOIN SubclassFeatureCrossRef ON SubclassFeatureCrossRef.featureId IS features.featureId
WHERE SubclassFeatureCrossRef.subclassId IS :subclassId AND features.grantedAtLevel <= :maxLevel
    """)
    abstract fun getSubclassFeatures(subclassId: Int, maxLevel: Int): List<Feature>

    @Query("DELETE FROM subclasses WHERE subclassId = :subclassId")
    abstract fun deleteSubclass(subclassId: Int)
}