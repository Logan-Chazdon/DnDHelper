package model.repositories

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.asFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import model.*
import model.database.daos.ClassDao
import model.database.daos.FeatureDao
import model.database.daos.SubclassDao
import model.junctionEntities.ClassFeatureCrossRef
import model.junctionEntities.ClassSubclassCrossRef
import model.junctionEntities.SubclassFeatureCrossRef
import model.pojos.NameAndIdPojo


actual class ClassRepository constructor(
    private val classDao: ClassDao,
    private val subclassDao: SubclassDao,
    private val featureDao: FeatureDao
)  {
    private val scope = CoroutineScope(Job())
    private val _classes =
        classDao.getAllClasses()

    actual fun getHomebrewClasses(): Flow<List<ClassEntity>> {
        return classDao.getHomebrewClasses().asFlow()
    }

    actual fun getClasses(): Flow<List<Class>> {
        return _classes.asFlow()
    }

    actual fun getSubclassLiveFeaturesById(id: Int) : Flow<List<Feature>> {
        return subclassDao.getSubclassLiveFeaturesById(id).asFlow()
    }

    actual fun getSubclassesByClassId(id: Int): Flow<List<Subclass>> {
        return subclassDao.getSubclassesByClassId(id).asFlow()
    }

    actual fun createDefaultClass(): Int {
        return classDao.insertClass(
            ClassEntity(
                name = "Homebrew class",
                startingGoldD4s = 4,
                startingGoldMultiplier = 10,
                subclassLevel = 1
            )
        ).toInt()
    }

    actual fun insertClass(classEntity: ClassEntity) {
        classDao.insertClass(classEntity)
    }

    actual fun deleteClass(id: Int) {
        classDao.deleteClass(id)
    }

    actual fun insertClassFeatureCrossRef(classFeatureCrossRef: ClassFeatureCrossRef) {
        classDao.insertClassFeatureCrossRef(classFeatureCrossRef)
    }

    actual fun getSpellsByClassId(classId: Int): MutableList<Spell> {
        return classDao.getSpellsByClassId(classId)
    }

    actual fun createDefaultSubclass(): Int {
        return subclassDao.insertSubclass(
            SubclassEntity(
                "",
                spellCasting = null,
                spellAreFree = true,
                isHomebrew = true
            )
        )
    }

    actual fun getSubclass(it: Int): Flow<Subclass> {
        return subclassDao.getSubclass(it).asFlow()
    }

    actual fun removeSubclassFeatureCrossRef(subclassFeatureCrossRef: SubclassFeatureCrossRef) {
        subclassDao.removeSubclassFeatureCrossRef(subclassFeatureCrossRef)
    }

    actual fun insertSubclassFeatureCrossRef(subclassFeatureCrossRef: SubclassFeatureCrossRef) {
        subclassDao.insertSubclassFeatureCrossRef(subclassFeatureCrossRef)
    }

    actual fun insertSubclass(subclassEntity: SubclassEntity) {
        subclassDao.insertSubclass(subclassEntity)
    }

    actual fun insertClassSubclassCrossRef(classSubclassCrossRef: ClassSubclassCrossRef) {
        classDao.insertClassSubclassId(classSubclassCrossRef)
    }

    actual fun removeClassFeatureCrossRef(classFeatureCrossRef: ClassFeatureCrossRef) {
        classDao.removeClassFeatureCrossRef(classFeatureCrossRef)
    }

    actual fun removeClassSubclassCrossRef(classSubclassCrossRef: ClassSubclassCrossRef) {
        classDao.removeClassSubclassCrossRef(classSubclassCrossRef)
    }

    actual fun getLevelPath(id: Int): MutableList<Feature> {
        val features = classDao.getUnfilledLevelPath(id)
        featureDao.fillOutFeatureListWithoutChosen(features)
        return features
    }

    actual fun getClass(id: Int): Flow<Class> {
        val result = MediatorLiveData<Class>()
        result.addSource(classDao.getUnfilledClass(id)) { entity ->
            scope.launch {
                if (entity != null) {
                    result.postValue(
                        Class(
                            entity,
                            getLevelPath(id)
                        )
                    )
                }
            }
        }
        return result.asFlow()
    }

    actual fun getAllClassNameAndIds(): Flow<List<NameAndIdPojo>> {
        return classDao.allClassesNamesAndIds().asFlow()
    }

    actual fun getHomebrewSubclasses(): Flow<List<SubclassEntity>> {
        return subclassDao.getHomebrewSubclasses().asFlow()
    }

    actual fun getSubclassClasses(id: Int): Flow<List<NameAndIdPojo>> {
        return classDao.getSubclassClasses(id).asFlow()
    }

    actual fun deleteSubclass(subclassId: Int) {
        subclassDao.deleteSubclass(subclassId)
    }

    actual companion object {
        actual val getMulticlassSpellSlots = fun(levels: Int): MutableList<Resource> {
            val result = mutableListOf<Resource>()
            multiclassSpellSlots[levels - 1].forEachIndexed { index, i ->
                result.add(
                    Resource(SpellRepository.allSpellLevels[index].second, i, i.toString(), i.toString())
                )
            }
            return result
        }
        actual val multiclassSpellSlots = mutableListOf<List<Int>>().apply {
            add(listOf(2))
            add(listOf(3))
            add(listOf(4, 2))
            add(listOf(4, 3))
            add(listOf(4, 3, 2))
            add(listOf(4, 3, 3))
            add(listOf(4, 3, 3, 1))
            add(listOf(4, 3, 3, 2))
            add(listOf(4, 3, 3, 3, 1))
            add(listOf(4, 3, 3, 3, 2))
            add(listOf(4, 3, 3, 3, 2, 1))
            add(listOf(4, 3, 3, 3, 2, 1))
            add(listOf(4, 3, 3, 3, 2, 1, 1))
            add(listOf(4, 3, 3, 3, 2, 1, 1))
            add(listOf(4, 3, 3, 3, 2, 1, 1, 1))
            add(listOf(4, 3, 3, 3, 2, 1, 1, 1))
            add(listOf(4, 3, 3, 3, 2, 1, 1, 1, 1))
            add(listOf(4, 3, 3, 3, 3, 1, 1, 1, 1))
            add(listOf(4, 3, 3, 3, 3, 2, 1, 1, 1))
            add(listOf(4, 3, 3, 3, 3, 2, 2, 1, 1))
        }
    }
}