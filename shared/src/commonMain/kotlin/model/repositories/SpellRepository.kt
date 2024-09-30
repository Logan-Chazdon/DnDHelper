package model.repositories

import kotlinx.coroutines.flow.Flow
import model.Spell
import model.database.daos.SpellDao
import model.pojos.NameAndIdPojo


 class SpellRepository {
    val spellDao: SpellDao

     constructor(spellDao: SpellDao) {
        this.spellDao = spellDao
        this._spells = spellDao.getAllSpells()
        this._homebrewSpells = spellDao.getHomebrewSpells()
    }

    private val _spells: Flow<List<Spell>>
    private val _homebrewSpells: Flow<List<Spell>>

     fun getLiveSpells(): Flow<List<Spell>> {
        return _spells
    }

     fun getHomebrewSpells(): Flow<List<Spell>> {
        return _homebrewSpells
    }


     fun getLiveSpell(id: Int): Flow<Spell> {
        return spellDao.getLiveSpell(id)
    }

     fun insertSpell(spell: Spell) {
        spellDao.insertSpell(spell)
    }

     fun createDefaultSpell(): Int {
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
        )
    }

     fun getSpellClasses(id: Int): Flow<List<NameAndIdPojo>> {
        return spellDao.getSpellClasses(id)
    }

     fun removeClassSpellCrossRef(
        classId: Int,
        spellId: Int
    ) {
        spellDao.removeClassSpellCrossRef(
               classId = classId,
               spellId = spellId
        )
    }

     fun addClassSpellCrossRef(
        classId: Int,
        spellId: Int
    ) {
        spellDao.addClassSpellCrossRef(
            classId = classId,
            spellId = spellId
        )
    }

     fun deleteSpell(id: Int) {
        spellDao.removeSpellById(id)
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