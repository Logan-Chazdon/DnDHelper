package model.sync.workers

import com.google.gson.reflect.TypeToken
import model.ClassEntity
import model.SubclassEntity

class PostClassWorker : SyncWorker<ClassEntity>(TypeToken.get(ClassEntity::class.java)) {
    override suspend fun sync(it: ClassEntity) {
        classService.insertClass(it)
    }
}

class DeleteClassWorker : SyncWorker<Int>(TypeToken.get(Int::class.java)) {
    override suspend fun sync(it: Int) {
        classService.deleteClass(it)
    }
}

class PostClassFeatureCrossRefWorker : SyncWorker<Pair<Int, Int>>(intIdPairToken) {
    override suspend fun sync(it: Pair<Int, Int>) {
        classService.insertClassFeatureCrossRef(
            featureId = it.first,
            id = it.second
        )
    }
}

class PostSubclassWorker : SyncWorker<SubclassEntity>(TypeToken.get(SubclassEntity::class.java)) {
    override suspend fun sync(it: SubclassEntity) {
        subclassService.insertSubclass(it)
    }
}

class DeleteSubclassFeatureCrossRefWorker : SyncWorker<Pair<Int, Int>>(intIdPairToken) {
    override suspend fun sync(it: Pair<Int, Int>) {
        subclassService.removeSubclassFeatureCrossRef(
            subclassId = it.second,
            featureId = it.first
        )
    }
}

class PostSubclassFeatureCrossRefWorker : SyncWorker<Pair<Int, Int>>(intIdPairToken) {
    override suspend fun sync(it: Pair<Int, Int>) {
        subclassService.insertSubclassFeatureCrossRef(
            subclassId = it.first,
            featureId = it.second
        )
    }
}

class PostClassSubclassCrossRefWorker : SyncWorker<Pair<Int, Int>>(intIdPairToken) {
    override suspend fun sync(it: Pair<Int, Int>) {
        classService.insertClassSubclassId(
            classId = it.first,
            subclassId = it.second
        )
    }
}

class DeleteClassFeatureCrossRefWorker : SyncWorker<Pair<Int, Int>>(intIdPairToken) {
    override suspend fun sync(it: Pair<Int, Int>) {
        classService.removeClassFeatureCrossRef(
            featureId = it.second,
            id = it.first
        )
    }
}

class DeleteClassSubclassCrossRefWorker : SyncWorker<Pair<Int, Int>>(intIdPairToken) {
    override suspend fun sync(it: Pair<Int, Int>) {
        classService.removeClassSubclassCrossRef(
            classId = it.first,
            subclassId = it.second
        )
    }
}

class DeleteSubclassWorker : SyncWorker<Int>(TypeToken.get(Int::class.java)) {
    override suspend fun sync(it: Int) {
        subclassService.deleteSubclass(it)
    }
}