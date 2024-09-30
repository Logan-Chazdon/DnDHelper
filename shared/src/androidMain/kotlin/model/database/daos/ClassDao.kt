package model.database.daos

import androidx.room.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import model.*
import model.pojos.NameAndIdPojo

@Dao
actual abstract class ClassDao {
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
    actual abstract fun getUnfilledLevelPath(id: Int): MutableList<Feature>


    actual fun insertClass(classEntity: ClassEntity): Int {
        val id = insertClassOrIgnore(classEntity.asTable()).toInt()
        if(id == -1) {
            updateClass(classEntity.asTable())
            return classEntity.id
        }
        return id
    }

    @Update
    protected abstract fun updateClass(classEntity: ClassEntityTable)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract fun insertClassOrIgnore(classEntity: ClassEntityTable) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertClassFeatureCrossRef(ref: ClassFeatureCrossRef)
    actual fun insertClassFeatureCrossRef(featureId: Int, id: Int) {
        insertClassFeatureCrossRef(
            ClassFeatureCrossRef(
                featureId = featureId,
                id = id
            )
        )
    }

    @Delete
    abstract fun removeClassFeatureCrossRef(ref: ClassFeatureCrossRef)
    actual fun removeClassFeatureCrossRef(featureId: Int, id: Int) {
        removeClassFeatureCrossRef(
            ClassFeatureCrossRef(
                featureId = featureId,
                id = id
            )
        )
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertClassSpellCrossRef(ref: ClassSpellCrossRef)

    @Delete
    abstract fun removeClassSpellCrossRef(ref: ClassSpellCrossRef)

    actual fun getAllClasses(): Flow<List<Class>> {
        return getAllClassEntities().transform {
            GlobalScope.launch {
                val temp = mutableListOf<Class>()
                it.forEachIndexed { index, classEntity ->
                    temp.add(
                        index, Class(classEntity, mutableListOf())
                    )
                }
                emit(temp)
            }
        }
    }

    @Query("SELECT * FROM classes")
    protected abstract fun getAllClassEntities(): Flow<List<ClassEntity>>

    @Query("SELECT * FROM classes WHERE isHomebrew IS 1")
    actual abstract fun getHomebrewClasses(): Flow<List<ClassEntity>>

    @Query("DELETE FROM classes WHERE id IS :id")
    actual abstract fun deleteClass(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertClassSubclassId(classSubclassCrossRef: ClassSubclassCrossRef)

    @Query("SELECT * FROM classes WHERE id IS :id")
    actual abstract fun getUnfilledClass(id: Int): Flow<ClassEntity>

    @Query(
        """SELECT * FROM spells
JOIN ClassSpellCrossRef ON spells.id IS ClassSpellCrossRef.spellId
WHERE classId IS :classId"""
    )
    actual abstract fun getSpellsByClassId(classId: Int): MutableList<Spell>

    @Delete
    abstract fun removeClassSubclassCrossRef(classSubclassCrossRef: ClassSubclassCrossRef)
    actual fun removeClassSubclassCrossRef(classId: Int, subclassId: Int) {
        removeClassSubclassCrossRef(
            ClassSubclassCrossRef(
                classId = classId,
                subclassId = subclassId
            )
        )
    }

    //Note this function can return multiple classes by intention.
    //This is in case a user creates a new class with the same name as a different class.
    @Query("SELECT id FROM classes WHERE name IS :name")
    actual abstract fun getClassIdsByName(name: String) : List<Int>

    @Query("SELECT id, name FROM classes")
    actual abstract fun allClassesNamesAndIds(): Flow<List<NameAndIdPojo>>

    @Query("SELECT classes.name, classes.id FROM classes JOIN ClassSubclassCrossRef ON classId IS classes.id WHERE subclassId IS :id")
    actual abstract fun getSubclassClasses(id: Int): Flow<List<NameAndIdPojo>>

    actual fun insertClassSubclassId(classId: Int, subclassId: Int) {
        insertClassSubclassId(
            ClassSubclassCrossRef(
                classId = classId,
                subclassId = subclassId
            )
        )
    }
}