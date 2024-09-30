package model.database.daos

import kotlinx.coroutines.flow.Flow
import model.Class
import model.ClassEntity
import model.Feature
import model.Spell
import model.pojos.NameAndIdPojo

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect abstract class ClassDao {
    abstract fun getClassIdsByName(name: String): List<Int>
    fun getAllClasses(): Flow<List<Class>>
    abstract fun getHomebrewClasses(): Flow<List<ClassEntity>>
    fun insertClass(classEntity: ClassEntity) : Int
    abstract fun deleteClass(id: Int)
    fun insertClassFeatureCrossRef(featureId: Int, id: Int)
    abstract fun getSpellsByClassId(classId: Int): MutableList<Spell>
    fun insertClassSubclassId(classId: Int, subclassId: Int)
    fun removeClassFeatureCrossRef(featureId: Int, id: Int)
    fun removeClassSubclassCrossRef(classId: Int, subclassId: Int)
    abstract fun getUnfilledLevelPath(id: Int): MutableList<Feature>
    abstract fun getUnfilledClass(id: Int): Flow<ClassEntity>
    abstract fun allClassesNamesAndIds() : Flow<List<NameAndIdPojo>>
    abstract fun getSubclassClasses(id: Int): Flow<List<NameAndIdPojo>>
}