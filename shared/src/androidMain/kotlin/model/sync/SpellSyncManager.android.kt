package model.sync

import android.content.Context
import model.Spell
import model.sync.workers.DeleteClassSpellCrossRefWorker
import model.sync.workers.DeleteSpellWorker
import model.sync.workers.PostClassSpellCrossRefWorker
import model.sync.workers.PostSpellWorker

actual class SpellSyncManager(context: Context) : SyncManager(context){
    actual fun postSpell(spell: Spell) {
        pushSync<PostSpellWorker>(gson.toJson(
            spell
        ))
    }

    actual fun deleteClassSpellCrossRef(classId: Int, spellId: Int) {
        pushSync<DeleteClassSpellCrossRefWorker>(gson.toJson(
            Pair(spellId, classId)
        ))
    }

    actual fun postClassSpellCrossRef(classId: Int, spellId: Int) {
        pushSync<PostClassSpellCrossRefWorker>(gson.toJson(
            Pair(spellId, classId)
        ))
    }

    actual fun deleteSpellById(id: Int) {
        pushSync<DeleteSpellWorker>(gson.toJson(
            id
        ))
    }
}