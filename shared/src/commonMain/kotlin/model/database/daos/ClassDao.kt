package model.database.daos

import kotlinx.coroutines.flow.Flow
import model.Class
import model.ClassEntity
import model.Spell
import model.pojos.NameAndIdPojo

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect abstract class ClassDao {
    abstract suspend fun getClassIdsByName(name: String): List<Int>
    fun getAllClasses(): Flow<List<Class>>
    abstract fun getHomebrewClasses(): Flow<List<ClassEntity>>
    suspend fun insertClass(classEntity: ClassEntity) : Int
    abstract suspend fun deleteClass(id: Int)
    suspend fun insertClassFeatureCrossRef(featureId: Int, id: Int)
    abstract suspend fun getSpellsByClassId(classId: Int): MutableList<Spell>
    suspend fun insertClassSubclassId(classId: Int, subclassId: Int)
    suspend fun removeClassFeatureCrossRef(featureId: Int, id: Int)
    suspend fun removeClassSubclassCrossRef(classId: Int, subclassId: Int)
    abstract fun getUnfilledClass(id: Int): Flow<ClassEntity>
    abstract fun allClassesNamesAndIds() : Flow<List<NameAndIdPojo>>
    abstract fun getSubclassClasses(id: Int): Flow<List<NameAndIdPojo>>
}