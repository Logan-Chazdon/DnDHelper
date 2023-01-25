package gmail.loganchazdon.dndhelper.model.repositories

import androidx.lifecycle.LiveData
import gmail.loganchazdon.dndhelper.model.Spell
import gmail.loganchazdon.dndhelper.model.database.daos.SpellDao
import javax.inject.Inject

class SpellRepository @Inject constructor(
    private val spellDao: SpellDao
) {
    private val _spells = spellDao.getAllSpells()

    fun getLiveSpells(): LiveData<List<Spell>> {
        return _spells
    }

    fun getAllSpells(): List<Spell> {
        return _spells.value ?: listOf()
    }


    companion object {
        val allSpellLevels = listOf(
            Pair(1, "First Level"),
            Pair(2, "Second Level"),
            Pair(3, "Third Level"),
            Pair(4, "Forth Level"),
            Pair(5, "Fifth Level"),
            Pair(6, "Sixth Level"),
            Pair(7, "Seventh Level"),
            Pair(8, "Eighth Level"),
            Pair(9, "Ninth Level"),
        )
    }
}