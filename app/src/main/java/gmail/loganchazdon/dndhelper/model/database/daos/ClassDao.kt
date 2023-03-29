package gmail.loganchazdon.dndhelper.model.database.daos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.room.*
import gmail.loganchazdon.dndhelper.model.Class
import gmail.loganchazdon.dndhelper.model.ClassEntity
import gmail.loganchazdon.dndhelper.model.Feature
import gmail.loganchazdon.dndhelper.model.Spell
import gmail.loganchazdon.dndhelper.model.junctionEntities.ClassFeatureCrossRef
import gmail.loganchazdon.dndhelper.model.junctionEntities.ClassSpellCrossRef
import gmail.loganchazdon.dndhelper.model.junctionEntities.ClassSubclassCrossRef
import gmail.loganchazdon.dndhelper.model.pojos.NameAndIdPojo
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Dao
abstract class ClassDao {
    @Query(
        """SELECT * FROM features
JOIN ClassFeatureCrossRef ON ClassFeatureCrossRef.featureId IS features.featureId
WHERE ClassFeatureCrossRef.id IS :classId AND features.grantedAtLevel <= :maxLevel
    """
    )
    abstract fun getClassFeatures(classId: Int, maxLevel: Int = 20): MutableList<Feature>

    @Query(
        """SELECT * FROM features
JOIN ClassFeatureCrossRef ON ClassFeatureCrossRef.featureId IS features.featureId
WHERE ClassFeatureCrossRef.id IS :id"""
    )
    abstract fun getUnfilledLevelPath(id: Int): MutableList<Feature>


    fun insertClass(classEntity: ClassEntity): Int {
        val id = insertClassOrIgnore(classEntity).toInt()
        if(id == -1) {
            updateClass(classEntity)
            return classEntity.id
        }
        return id
    }

    @Update
    protected abstract fun updateClass(classEntity: ClassEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract fun insertClassOrIgnore(classEntity: ClassEntity) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertClassFeatureCrossRef(ref: ClassFeatureCrossRef)

    @Delete
    abstract fun removeClassFeatureCrossRef(ref: ClassFeatureCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertClassSpellCrossRef(ref: ClassSpellCrossRef)

    @Delete
    abstract fun removeClassSpellCrossRef(ref: ClassSpellCrossRef)

    fun getAllClasses(): LiveData<List<Class>> {
        val classes = MediatorLiveData<List<Class>>()
        classes.addSource(getAllClassEntities()) {
            GlobalScope.launch {
                val temp = mutableListOf<Class>()
                it.forEachIndexed { index, classEntity ->
                    temp.add(
                        index, Class(classEntity, mutableListOf())
                    )
                }
                classes.postValue(temp)
            }
        }
        return classes
    }

    @Query("SELECT * FROM classes")
    protected abstract fun getAllClassEntities(): LiveData<List<ClassEntity>>

    @Query("SELECT * FROM classes WHERE isHomebrew IS 1")
    abstract fun getHomebrewClasses(): LiveData<List<ClassEntity>>

    @Query("DELETE FROM classes WHERE id IS :id")
    abstract fun deleteClass(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertClassSubclassId(classSubclassCrossRef: ClassSubclassCrossRef)

    @Query("SELECT * FROM classes WHERE id IS :id")
    abstract fun getUnfilledClass(id: Int): LiveData<ClassEntity>

    @Query(
        """SELECT * FROM spells
JOIN ClassSpellCrossRef ON spells.id IS ClassSpellCrossRef.spellId
WHERE classId IS :classId"""
    )
    abstract fun getSpellsByClassId(classId: Int): MutableList<Spell>

    @Delete
    abstract fun removeClassSubclassCrossRef(classSubclassCrossRef: ClassSubclassCrossRef)

    //Note this function can return multiple classes by intention.
    //This is in case a user creates a new class with the same name as a different class.
    @Query("SELECT id FROM classes WHERE name IS :name")
    abstract fun getClassIdsByName(name: String) : List<Int>

    @Query("SELECT id, name FROM classes")
    abstract fun allClassesNamesAndIds(): LiveData<List<NameAndIdPojo>>

    @Query("SELECT classes.name, classes.id FROM classes JOIN ClassSubclassCrossRef ON classId IS classes.id WHERE subclassId IS :id")
    abstract fun getSubclassClasses(id: Int): LiveData<List<NameAndIdPojo>>
}