package model.repositories

import androidx.lifecycle.asFlow
import kotlinx.coroutines.flow.Flow
import model.Spell
import model.database.daos.SpellDao
import model.junctionEntities.ClassSpellCrossRef
import model.pojos.NameAndIdPojo


actual class SpellRepository constructor(
    private val spellDao: SpellDao
) {
    private val _spells = spellDao.getAllSpells()
    private val _homebrewSpells = spellDao.getHomebrewSpells()

    actual fun getLiveSpells(): Flow<List<Spell>> {
        return _spells.asFlow()
    }
    
    actual fun getHomebrewSpells(): Flow<List<Spell>> {
        return _homebrewSpells.asFlow()
    }

    actual fun getAllSpells(): List<Spell> {
        return _spells.value ?: listOf()
    }

    actual fun getLiveSpell(id: Int): Flow<Spell> {
        return spellDao.getLiveSpell(id).asFlow()
    }

    actual fun insertSpell(spell: Spell) {
        spellDao.insertSpell(spell)
    }

    actual fun createDefaultSpell(): Int {
        return spellDao.insertSpell(
            Spell(
                name = "",
                level = 0,
                components = emptyList(),
                itemComponents = emptyList(),
                school = "Evocation",
                desc = "",
                area = "",
                castingTime = "",
                duration = "",
                classes = emptyList(),
                damage = "",
                isRitual = false,
                range = ""
            )
        ).toInt()
    }

    actual fun getSpellClasses(id: Int): Flow<List<NameAndIdPojo>> {
        return spellDao.getSpellClasses(id).asFlow()
    }

    actual fun removeClassSpellCrossRef(ref: ClassSpellCrossRef) {
        spellDao.removeClassSpellCrossRef(ref)
    }

    actual fun addClassSpellCrossRef(ref: ClassSpellCrossRef) {
        spellDao.addClassSpellCrossRef(ref)
    }

    actual fun deleteSpell(id: Int) {
        spellDao.removeSpellById(id)
    }


    actual companion object {
        actual val allSpellLevels = listOf(
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