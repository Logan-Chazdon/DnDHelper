package model.sync

import model.Spell

actual class SpellSyncManager {
    actual fun postSpell(spell: Spell) {
    }

    actual fun deleteClassSpellCrossRef(classId: Int, spellId: Int) {
    }

    actual fun postClassSpellCrossRef(classId: Int, spellId: Int) {
    }

    actual fun deleteSpellById(id: Int) {
    }
}