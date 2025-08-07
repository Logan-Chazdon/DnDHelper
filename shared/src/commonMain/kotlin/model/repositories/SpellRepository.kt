package model.repositories

import kotlinx.coroutines.flow.Flow
import model.Spell
import model.database.daos.SpellDao
import model.pojos.NameAndIdPojo
import model.sync.SpellSyncManager


class SpellRepository {
    private val spellDao: SpellDao
    private val spellSyncManager: SpellSyncManager

    constructor(spellDao: SpellDao, spellSyncManager: SpellSyncManager) {
        this.spellDao = spellDao
        this.spellSyncManager = spellSyncManager
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

    suspend fun insertSpell(spell: Spell) {
        spellSyncManager.postSpell(spell)
        spellDao.insertSpell(spell)
    }

    suspend fun createDefaultSpell(): Int {
        val default = Spell(
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
        val id = spellDao.insertSpell(default)
        spellSyncManager.postSpell(default.apply { this.id = id})
        return id
    }

    fun getSpellClasses(id: Int): Flow<List<NameAndIdPojo>> {
        return spellDao.getSpellClasses(id)
    }

    suspend fun removeClassSpellCrossRef(
        classId: Int,
        spellId: Int
    ) {
        spellSyncManager.deleteClassSpellCrossRef(
            classId = classId,
            spellId = spellId
        )
        spellDao.removeClassSpellCrossRef(
            classId = classId,
            spellId = spellId
        )
    }

    suspend fun addClassSpellCrossRef(
        classId: Int,
        spellId: Int
    ) {
        spellSyncManager.postClassSpellCrossRef(
            classId = classId,
            spellId = spellId
        )
        spellDao.addClassSpellCrossRef(
            classId = classId,
            spellId = spellId
        )
    }

    suspend fun deleteSpell(id: Int) {
        spellSyncManager.deleteSpellById(id)
        spellDao.removeSpellById(id)
    }
}