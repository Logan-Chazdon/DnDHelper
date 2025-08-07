package model.sync

import model.Spell

expect class SpellSyncManager {
    fun postSpell(spell: Spell)
    fun deleteClassSpellCrossRef(classId: Int, spellId: Int)
    fun postClassSpellCrossRef(classId: Int, spellId: Int)
    fun deleteSpellById(id: Int)
}