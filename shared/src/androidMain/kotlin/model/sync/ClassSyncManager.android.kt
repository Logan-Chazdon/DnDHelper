package model.sync

import android.content.Context
import model.ClassEntity
import model.SubclassEntity
import model.sync.workers.*

actual class ClassSyncManager(context: Context) : SyncManager(context) {
    actual fun postClass(apply: ClassEntity) {
        pushSync<PostClassWorker>(gson.toJson(
            apply
        ))
    }

    actual fun deleteClass(id: Int) {
        pushSync<DeleteClassWorker>(gson.toJson(
            id
        ))
    }

    actual fun postClassFeatureCrossRef(featureId: Int, id: Int) {
        pushSync<PostClassFeatureCrossRefWorker>(gson.toJson(
            Pair(featureId, id)
        ))
    }

    actual fun postSubclass(apply: SubclassEntity) {
        pushSync<PostSubclassWorker>(gson.toJson(
            apply
        ))
    }

    actual fun deleteSubclassFeatureCrossRef(subclassId: Int, featureId: Int) {
        pushSync<DeleteSubclassFeatureCrossRefWorker>(gson.toJson(
            Pair(featureId, subclassId)
        ))
    }

    actual fun postSubclassFeatureCrossRef(subclassId: Int, featureId: Int) {
        pushSync<PostSubclassFeatureCrossRefWorker>(gson.toJson(
            Pair(featureId, subclassId)
        ))
    }

    actual fun postClassSubclassCrossRef(classId: Int, subclassId: Int) {
        pushSync<PostClassSubclassCrossRefWorker>(gson.toJson(
            Pair(classId, subclassId)
        ))
    }

    actual fun deleteClassFeatureCrossRef(classId: Int, featureId: Int) {
        pushSync<DeleteClassFeatureCrossRefWorker>(gson.toJson(
            Pair(classId, featureId)
        ))
    }

    actual fun deleteClassSubclassCrossRef(classId: Int, subclassId: Int) {
        pushSync<DeleteClassSubclassCrossRefWorker>(gson.toJson(
            Pair(classId, subclassId)
        ))
    }

    actual fun deleteSubclass(subclassId: Int) {
        pushSync<DeleteSubclassWorker>(gson.toJson(
            subclassId
        ))
    }
}