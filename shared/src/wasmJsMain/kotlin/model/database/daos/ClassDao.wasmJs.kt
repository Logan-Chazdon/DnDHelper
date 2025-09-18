package model.database.daos

import kotlinx.coroutines.flow.Flow
import model.Class
import model.ClassEntity
import model.Spell
import model.pojos.NameAndIdPojo
import services.ClassService

actual abstract class ClassDao {
    actual abstract suspend fun getClassIdsByName(name: String): List<Int>
    protected val classService: ClassService

    constructor(classService: ClassService) {
        this.classService = classService
    }

    actual fun getAllClasses(): Flow<List<Class>> {
        return classService.getAllClasses()
    }

    actual abstract fun getHomebrewClasses(): Flow<List<ClassEntity>>
    actual suspend fun insertClass(classEntity: ClassEntity): Int {
        return classService.insertClass(classEntity)
    }

    actual abstract suspend fun deleteClass(id: Int)
    actual suspend fun insertClassFeatureCrossRef(featureId: Int, id: Int) {
        classService.insertClassFeatureCrossRef(featureId, id)
    }

    actual abstract suspend fun getSpellsByClassId(classId: Int): MutableList<Spell>
    actual suspend fun insertClassSubclassId(classId: Int, subclassId: Int) {
        classService.insertClassSubclassId(classId, subclassId)
    }

    actual suspend fun removeClassFeatureCrossRef(featureId: Int, id: Int) {
        classService.removeClassFeatureCrossRef(featureId, id)
    }

    actual suspend fun removeClassSubclassCrossRef(classId: Int, subclassId: Int) {
        classService.removeClassSubclassCrossRef(classId, subclassId)
    }


    actual abstract fun getUnfilledClass(id: Int): Flow<ClassEntity>
    actual abstract fun allClassesNamesAndIds(): Flow<List<NameAndIdPojo>>
    actual abstract fun getSubclassClasses(id: Int): Flow<List<NameAndIdPojo>>
}

class ClassDaoImpl(classService: ClassService) : ClassDao(classService) {
    override suspend fun getClassIdsByName(name: String): List<Int> {
        return classService.getClassIdsByName(name)
    }

    override fun getHomebrewClasses(): Flow<List<ClassEntity>> {
        return classService.getHomebrewClasses()
    }

    override suspend fun deleteClass(id: Int) {
        return classService.deleteClass(id)
    }

    override suspend fun getSpellsByClassId(classId: Int): MutableList<Spell> {
        return classService.getSpellsByClassId(classId)
    }


    override fun getUnfilledClass(id: Int): Flow<ClassEntity> {
        return classService.getUnfilledClass(id)
    }

    override fun allClassesNamesAndIds(): Flow<List<NameAndIdPojo>> {
        return classService.allClassesNamesAndIds()
    }

    override fun getSubclassClasses(id: Int): Flow<List<NameAndIdPojo>> {
        return classService.getSubclassClasses(id)
    }
}