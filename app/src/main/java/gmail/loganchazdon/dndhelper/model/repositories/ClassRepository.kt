package gmail.loganchazdon.dndhelper.model.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import gmail.loganchazdon.dndhelper.model.*
import gmail.loganchazdon.dndhelper.model.database.daos.ClassDao
import gmail.loganchazdon.dndhelper.model.database.daos.FeatureDao
import gmail.loganchazdon.dndhelper.model.database.daos.SubclassDao
import gmail.loganchazdon.dndhelper.model.junctionEntities.ClassFeatureCrossRef
import gmail.loganchazdon.dndhelper.model.junctionEntities.ClassSubclassCrossRef
import gmail.loganchazdon.dndhelper.model.junctionEntities.SubclassFeatureCrossRef
import gmail.loganchazdon.dndhelper.model.pojos.NameAndIdPojo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class ClassRepository @Inject constructor(
    private val classDao: ClassDao,
    private val subclassDao: SubclassDao,
    private val featureDao: FeatureDao
) {
    private val scope = CoroutineScope(Job())
    private val _classes =
        classDao.getAllClasses()

    fun getHomebrewClasses(): LiveData<List<ClassEntity>> {
        return classDao.getHomebrewClasses()
    }

    fun getClasses(): LiveData<List<Class>> {
        return _classes
    }

    fun getSubclassesByClassId(id: Int): LiveData<List<Subclass>> {
        return subclassDao.getSubclassesByClassId(id)
    }

    fun createDefaultClass(): Int {
        return classDao.insertClass(
            ClassEntity(
                name = "Homebrew class",
                startingGoldD4s = 4,
                startingGoldMultiplier = 10,
                subclassLevel = 1
            )
        ).toInt()
    }

    fun insertClass(classEntity: ClassEntity) {
        classDao.insertClass(classEntity)
    }

    fun deleteClass(id: Int) {
        classDao.deleteClass(id)
    }

    fun insertClassFeatureCrossRef(classFeatureCrossRef: ClassFeatureCrossRef) {
        classDao.insertClassFeatureCrossRef(classFeatureCrossRef)
    }

    fun getSpellsByClassId(classId: Int): MutableList<Spell> {
        return classDao.getSpellsByClassId(classId)
    }

    fun createDefaultSubclass(): Int {
        return subclassDao.insertSubclass(
            SubclassEntity(
                "",
                spellCasting = null,
                spellAreFree = true,
                isHomebrew = true
            )
        )
    }

    fun getSubclass(it: Int): LiveData<Subclass> {
        return subclassDao.getSubclass(it)
    }

    fun removeSubclassFeatureCrossRef(subclassFeatureCrossRef: SubclassFeatureCrossRef) {
        subclassDao.removeSubclassFeatureCrossRef(subclassFeatureCrossRef)
    }

    fun insertSubclassFeatureCrossRef(subclassFeatureCrossRef: SubclassFeatureCrossRef) {
        subclassDao.insertSubclassFeatureCrossRef(subclassFeatureCrossRef)
    }

    fun insertSubclass(subclassEntity: SubclassEntity) {
        subclassDao.insertSubclass(subclassEntity)
    }

    fun insertClassSubclassCrossRef(classSubclassCrossRef: ClassSubclassCrossRef) {
        classDao.insertClassSubclassId(classSubclassCrossRef)
    }

    fun removeClassFeatureCrossRef(classFeatureCrossRef: ClassFeatureCrossRef) {
        classDao.removeClassFeatureCrossRef(classFeatureCrossRef)
    }

    fun removeClassSubclassCrossRef(classSubclassCrossRef: ClassSubclassCrossRef) {
        classDao.removeClassSubclassCrossRef(classSubclassCrossRef)
    }

    private fun getLevelPath(id: Int): MutableList<Feature> {
        val features = classDao.getUnfilledLevelPath(id)
        featureDao.fillOutFeatureListWithoutChosen(features)
        return features
    }

    fun getClass(id: Int): LiveData<Class> {
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
        return result
    }

    fun getAllClassNameAndIds(): LiveData<List<NameAndIdPojo>> {
        return classDao.allClassesNamesAndIds()
    }

    fun getHomebrewSubclasses(): LiveData<List<SubclassEntity>> {
        return subclassDao.getHomebrewSubclasses()
    }

    fun getSubclassClasses(id: Int): LiveData<List<NameAndIdPojo>> {
        return classDao.getSubclassClasses(id)
    }

    companion object {
        val getMulticlassSpellSlots = fun(levels: Int): MutableList<Resource> {
            val result = mutableListOf<Resource>()
            multiclassSpellSlots[levels - 1].forEachIndexed { index, i ->
                result.add(
                    Resource(SpellRepository.allSpellLevels[index].second, i, i.toString(), i.toString())
                )
            }
            return result
        }
        private val multiclassSpellSlots = mutableListOf<List<Int>>().apply {
            add(listOf(2))
            add(listOf(3))
            add(listOf(4))
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