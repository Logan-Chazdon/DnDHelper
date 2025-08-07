package model.sync

import model.ClassEntity
import model.SubclassEntity

expect class ClassSyncManager {
    fun postClass(apply: ClassEntity)
    fun deleteClass(id: Int)
    fun postClassFeatureCrossRef(featureId: Int, id: Int)
    fun postSubclass(apply: SubclassEntity)
    fun deleteSubclassFeatureCrossRef(subclassId: Int, featureId: Int)
    fun postSubclassFeatureCrossRef(subclassId: Int, featureId: Int)
    fun postClassSubclassCrossRef(classId: Int, subclassId: Int)
    fun deleteClassFeatureCrossRef(classId: Int, featureId: Int)
    fun deleteClassSubclassCrossRef(classId: Int, subclassId: Int)
    fun deleteSubclass(subclassId: Int)
}