package model.sync.workers

import com.google.gson.reflect.TypeToken
import model.Spell

class PostSpellWorker : SyncWorker<Spell>(TypeToken.get(Spell::class.java)) {
    override suspend fun sync(it: Spell) {
        spellService.insertSpell(it)
    }
}

class DeleteClassSpellCrossRefWorker : SyncWorker<Pair<Int, Int>>(intIdPairToken) {
    override suspend fun sync(it: Pair<Int, Int>) {
        spellService.removeClassSpellCrossRef(
            classId = it.second,
            spellId = it.first
        )
    }
}

class PostClassSpellCrossRefWorker : SyncWorker<Pair<Int, Int>>(intIdPairToken) {
    override suspend fun sync(it: Pair<Int, Int>) {
        spellService.addClassSpellCrossRef(
            classId = it.second,
            spellId = it.first
        )
    }
}

class DeleteSpellWorker : SyncWorker<Int>(TypeToken.get(Int::class.java)) {
    override suspend fun sync(it: Int) {
        spellService.removeSpellById(it)
    }
}
