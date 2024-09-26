package model.repositories

import kotlinx.coroutines.flow.Flow
import model.*
import model.junctionEntities.ClassFeatureCrossRef
import model.junctionEntities.ClassSubclassCrossRef
import model.junctionEntities.SubclassFeatureCrossRef
import model.pojos.NameAndIdPojo



@Suppress("NO_ACTUAL_FOR_EXPECT")
expect class ClassRepository {
    fun getHomebrewClasses(): Flow<List<ClassEntity>>
    fun getClasses(): Flow<List<Class>>
    fun getSubclassLiveFeaturesById(id: Int) : Flow<List<Feature>>
    fun getSubclassesByClassId(id: Int): Flow<List<Subclass>>
    fun createDefaultClass(): Int
    fun insertClass(classEntity: ClassEntity)
    fun deleteClass(id: Int)
    fun insertClassFeatureCrossRef(classFeatureCrossRef: ClassFeatureCrossRef)
    fun getSpellsByClassId(classId: Int): MutableList<Spell>
    fun createDefaultSubclass(): Int
    fun getSubclass(it: Int): Flow<Subclass>
    fun removeSubclassFeatureCrossRef(subclassFeatureCrossRef: SubclassFeatureCrossRef)
    fun insertSubclassFeatureCrossRef(subclassFeatureCrossRef: SubclassFeatureCrossRef)
    fun insertSubclass(subclassEntity: SubclassEntity)
    fun insertClassSubclassCrossRef(classSubclassCrossRef: ClassSubclassCrossRef)
    fun removeClassFeatureCrossRef(classFeatureCrossRef: ClassFeatureCrossRef)
    fun removeClassSubclassCrossRef(classSubclassCrossRef: ClassSubclassCrossRef)
    fun getLevelPath(id: Int): MutableList<Feature>
    fun getClass(id: Int): Flow<Class>
    fun getAllClassNameAndIds(): Flow<List<NameAndIdPojo>>
    fun getHomebrewSubclasses(): Flow<List<SubclassEntity>>
    fun getSubclassClasses(id: Int): Flow<List<NameAndIdPojo>>
    fun deleteSubclass(subclassId: Int)

    companion object {
        val getMulticlassSpellSlots: (Int) -> MutableList<Resource>
        val multiclassSpellSlots: MutableList<List<Int>>
    }
}