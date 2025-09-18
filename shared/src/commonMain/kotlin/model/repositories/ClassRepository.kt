package model.repositories

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import model.*
import model.database.daos.ClassDao
import model.database.daos.FeatureDao
import model.database.daos.SubclassDao
import model.pojos.NameAndIdPojo
import model.sync.ClassSyncManager


class ClassRepository {
    private val classDao: ClassDao
    private val subclassDao: SubclassDao
    private val featureDao: FeatureDao
    private val classSyncManager: ClassSyncManager

    constructor(
        classDao: ClassDao,
        subclassDao: SubclassDao,
        featureDao: FeatureDao,
        classSyncManager: ClassSyncManager
    ) {
        this.classDao = classDao
        this.subclassDao = subclassDao
        this.featureDao = featureDao
        this.scope = CoroutineScope(Job())
        this.classSyncManager = classSyncManager
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

    suspend fun createDefaultClass(): Int {
        val default = ClassEntity(
            name = "Homebrew class",
            startingGoldD4s = 4,
            startingGoldMultiplier = 10,
            subclassLevel = 1
        )
        val id = classDao.insertClass(
            default
        )

        classSyncManager.postClass(default.apply { this.id = id })

        return id
    }

    suspend fun insertClass(classEntity: ClassEntity) {
        classSyncManager.postClass(classEntity)
        classDao.insertClass(classEntity)
    }

    suspend fun deleteClass(id: Int) {
        classSyncManager.deleteClass(id)
        classDao.deleteClass(id)
    }

    suspend fun insertClassFeatureCrossRef(classId: Int, featureId: Int) {
        classSyncManager.postClassFeatureCrossRef(
            featureId = featureId,
            id = classId
        )

        classDao.insertClassFeatureCrossRef(
            featureId = featureId,
            id = classId
        )
    }

    suspend fun getSpellsByClassId(classId: Int): MutableList<Spell> {
        return classDao.getSpellsByClassId(classId)
    }

    suspend fun createDefaultSubclass(): Int {
        val default = SubclassEntity(
            "",
            spellCasting = null,
            spellAreFree = true,
            isHomebrew = true
        )

        val id = subclassDao.insertSubclass(
            default
        )

        classSyncManager.postSubclass(default.apply { this.subclassId = id})

        return id
    }

    fun getSubclass(it: Int): Flow<Subclass> {
        return subclassDao.getSubclass(it)
    }

    suspend fun removeSubclassFeatureCrossRef(subclassId: Int, featureId: Int) {
        classSyncManager.deleteSubclassFeatureCrossRef(
            subclassId = subclassId,
            featureId = featureId
        )

        subclassDao.removeSubclassFeatureCrossRef(
            subclassId = subclassId,
            featureId = featureId
        )
    }

    suspend fun insertSubclassFeatureCrossRef(subclassId: Int, featureId: Int) {
        classSyncManager.postSubclassFeatureCrossRef(
            subclassId = subclassId,
            featureId = featureId
        )

        subclassDao.insertSubclassFeatureCrossRef(
            subclassId = subclassId,
            featureId = featureId
        )
    }

    suspend fun insertSubclass(subclassEntity: SubclassEntity) {
        classSyncManager.postSubclass(subclassEntity)
        subclassDao.insertSubclass(subclassEntity)
    }

    suspend fun insertClassSubclassCrossRef(classId: Int, subclassId: Int) {
        classSyncManager.postClassSubclassCrossRef(classId, subclassId)
        classDao.insertClassSubclassId(
            classId = classId,
            subclassId = subclassId
        )
    }

    suspend fun removeClassFeatureCrossRef(classId: Int, featureId: Int) {
        classSyncManager.deleteClassFeatureCrossRef(classId, featureId)
        classDao.removeClassFeatureCrossRef(
            featureId = featureId,
            id = classId
        )
    }

    suspend fun removeClassSubclassCrossRef(classId: Int, subclassId: Int) {
        classSyncManager.deleteClassSubclassCrossRef(classId, subclassId)
        classDao.removeClassSubclassCrossRef(
            classId = classId,
            subclassId = subclassId
        )
    }

    fun getClass(id: Int): Flow<Class> {
        return classDao.getUnfilledClass(id).transform { entity ->
            emit(
                Class(
                    entity,
                    featureDao.getFilledLevelPath(id)
                )
            )
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

    suspend fun deleteSubclass(subclassId: Int) {
        classSyncManager.deleteSubclass(subclassId)
        subclassDao.deleteSubclass(subclassId)
    }

    companion object {
        val getMulticlassSpellSlots = fun(levels: Int): MutableList<Resource> {
            val result = mutableListOf<Resource>()
            multiclassSpellSlots[levels - 1].forEachIndexed { index, i ->
                result.add(
                    Resource(allSpellLevels[index].second, i, i.toString(), i.toString())
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