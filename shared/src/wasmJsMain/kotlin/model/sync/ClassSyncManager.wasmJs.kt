package model.sync

import model.ClassEntity
import model.SubclassEntity

actual class ClassSyncManager {
    actual fun postClass(apply: ClassEntity) {
    }

    actual fun deleteClass(id: Int) {
    }

    actual fun postClassFeatureCrossRef(featureId: Int, id: Int) {
    }

    actual fun postSubclass(apply: SubclassEntity) {
    }

    actual fun deleteSubclassFeatureCrossRef(subclassId: Int, featureId: Int) {
    }

    actual fun postSubclassFeatureCrossRef(subclassId: Int, featureId: Int) {
    }

    actual fun postClassSubclassCrossRef(classId: Int, subclassId: Int) {
    }

    actual fun deleteClassFeatureCrossRef(classId: Int, featureId: Int) {
    }

    actual fun deleteClassSubclassCrossRef(classId: Int, subclassId: Int) {
    }

    actual fun deleteSubclass(subclassId: Int) {
    }
}