package model.database.daos

import kotlinx.coroutines.flow.Flow
import model.Class
import model.ClassEntity
import model.Feature
import model.Spell
import model.pojos.NameAndIdPojo
import services.ClassService

actual abstract class ClassDao {
    actual abstract fun getClassIdsByName(name: String): List<Int>
    protected val classService: ClassService
    constructor(classService: ClassService) {
        this.classService = classService
    }
    actual fun getAllClasses(): Flow<List<Class>> {
        return classService.getAllClasses()
    }

    actual abstract fun getHomebrewClasses(): Flow<List<ClassEntity>>
    actual fun insertClass(classEntity: ClassEntity): Int {
        TODO("Not yet implemented")
    }

    actual abstract fun deleteClass(id: Int)
    actual fun insertClassFeatureCrossRef(featureId: Int, id: Int) {
    }

    actual abstract fun getSpellsByClassId(classId: Int): MutableList<Spell>
    actual fun insertClassSubclassId(classId: Int, subclassId: Int) {
    }

    actual fun removeClassFeatureCrossRef(featureId: Int, id: Int) {
    }

    actual fun removeClassSubclassCrossRef(classId: Int, subclassId: Int) {
    }

    actual abstract fun getUnfilledLevelPath(id: Int): MutableList<Feature>
    actual abstract fun getUnfilledClass(id: Int): Flow<ClassEntity>
    actual abstract fun allClassesNamesAndIds(): Flow<List<NameAndIdPojo>>
    actual abstract fun getSubclassClasses(id: Int): Flow<List<NameAndIdPojo>>
}

class ClassDaoImpl(classService: ClassService) : ClassDao(classService) {
    override fun getClassIdsByName(name: String): List<Int> {
        TODO("Not yet implemented")
    }

    override fun getHomebrewClasses(): Flow<List<ClassEntity>> {
        TODO("Not yet implemented")
    }

    override fun deleteClass(id: Int) {
        TODO("Not yet implemented")
    }

    override fun getSpellsByClassId(classId: Int): MutableList<Spell> {
        //TODO IMPL
        return mutableListOf()
    }

    override fun getUnfilledLevelPath(id: Int): MutableList<Feature> {
        //TODO IMPL
        return mutableListOf()
    }

    override fun getUnfilledClass(id: Int): Flow<ClassEntity> {
        return classService.getUnfilledClass(id)
    }

    override fun allClassesNamesAndIds(): Flow<List<NameAndIdPojo>> {
        TODO("Not yet implemented")
    }

    override fun getSubclassClasses(id: Int): Flow<List<NameAndIdPojo>> {
        TODO("Not yet implemented")
    }

}