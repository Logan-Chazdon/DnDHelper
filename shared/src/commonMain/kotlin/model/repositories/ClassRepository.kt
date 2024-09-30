package model.repositories

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import model.*
import model.database.daos.ClassDao
import model.database.daos.FeatureDao
import model.database.daos.SubclassDao
import model.pojos.NameAndIdPojo


class ClassRepository {
    private val classDao: ClassDao
    private val subclassDao: SubclassDao
    private val featureDao: FeatureDao

    constructor(classDao: ClassDao, subclassDao: SubclassDao, featureDao: FeatureDao) {
        this.classDao = classDao
        this.subclassDao = subclassDao
        this.featureDao = featureDao
        this.scope = CoroutineScope(Job())
        this._classes = classDao.getAllClasses()
    }

    private val scope: CoroutineScope
    private val _classes: Flow<List<Class>>

    fun getHomebrewClasses(): Flow<List<ClassEntity>> {
        return classDao.getHomebrewClasses()
    }

    fun getClasses(): Flow<List<Class>> {
        return _classes
    }

    fun getSubclassLiveFeaturesById(id: Int): Flow<List<Feature>> {
        return subclassDao.getSubclassLiveFeaturesById(id)
    }

    fun getSubclassesByClassId(id: Int): Flow<List<Subclass>> {
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

    fun insertClassFeatureCrossRef(classId: Int, featureId: Int) {
        classDao.insertClassFeatureCrossRef(
            featureId = featureId,
            id = classId
        )
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

    fun getSubclass(it: Int): Flow<Subclass> {
        return subclassDao.getSubclass(it)
    }

    fun removeSubclassFeatureCrossRef(subclassId: Int, featureId: Int) {
        subclassDao.removeSubclassFeatureCrossRef(
            subclassId = subclassId,
            featureId = featureId
        )
    }

    fun insertSubclassFeatureCrossRef(subclassId: Int, featureId: Int) {
        subclassDao.insertSubclassFeatureCrossRef(
            subclassId = subclassId,
            featureId = featureId
        )
    }

    fun insertSubclass(subclassEntity: SubclassEntity) {
        subclassDao.insertSubclass(subclassEntity)
    }

    fun insertClassSubclassCrossRef(classId: Int, subclassId: Int) {
        classDao.insertClassSubclassId(
            classId = classId,
            subclassId = subclassId
        )
    }

    fun removeClassFeatureCrossRef(classId: Int, featureId: Int) {
        classDao.removeClassFeatureCrossRef(
            featureId = featureId,
            id = classId
        )
    }

    fun removeClassSubclassCrossRef(classId: Int, subclassId: Int) {
        classDao.removeClassSubclassCrossRef(
            classId = classId,
            subclassId = subclassId
        )
    }

    fun getLevelPath(id: Int): MutableList<Feature> {
        val features = classDao.getUnfilledLevelPath(id)
        featureDao.fillOutFeatureListWithoutChosen(features)
        return features
    }

    fun getClass(id: Int): Flow<Class> {
        return classDao.getUnfilledClass(id).transform { entity ->
            scope.launch {
                emit(
                    Class(
                        entity,
                        getLevelPath(id)
                    )
                )
            }
        }
    }

    fun getAllClassNameAndIds(): Flow<List<NameAndIdPojo>> {
        return classDao.allClassesNamesAndIds()
    }

    fun getHomebrewSubclasses(): Flow<List<SubclassEntity>> {
        return subclassDao.getHomebrewSubclasses()
    }

    fun getSubclassClasses(id: Int): Flow<List<NameAndIdPojo>> {
        return classDao.getSubclassClasses(id)
    }

    fun deleteSubclass(subclassId: Int) {
        subclassDao.deleteSubclass(subclassId)
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
        val multiclassSpellSlots = mutableListOf<List<Int>>().apply {
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