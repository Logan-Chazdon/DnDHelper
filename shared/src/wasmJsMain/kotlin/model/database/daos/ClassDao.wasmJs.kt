package model.database.daos

import kotlinx.coroutines.flow.Flow
import model.Class
import model.ClassEntity
import model.Feature
import model.Spell
import model.pojos.NameAndIdPojo

actual abstract class ClassDao {
    actual abstract fun getClassIdsByName(name: String): List<Int>
    actual fun getAllClasses(): Flow<List<Class>> {
        TODO("Not yet implemented")
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

class ClassDaoImpl : ClassDao() {
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
        TODO("Not yet implemented")
    }

    override fun getUnfilledLevelPath(id: Int): MutableList<Feature> {
        TODO("Not yet implemented")
    }

    override fun getUnfilledClass(id: Int): Flow<ClassEntity> {
        TODO("Not yet implemented")
    }

    override fun allClassesNamesAndIds(): Flow<List<NameAndIdPojo>> {
        TODO("Not yet implemented")
    }

    override fun getSubclassClasses(id: Int): Flow<List<NameAndIdPojo>> {
        TODO("Not yet implemented")
    }

}