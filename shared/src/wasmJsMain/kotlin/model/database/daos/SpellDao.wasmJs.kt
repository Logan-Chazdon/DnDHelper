package model.database.daos

import kotlinx.coroutines.flow.Flow
import model.Spell
import model.pojos.NameAndIdPojo
import services.SpellService

actual abstract class SpellDao {
    protected val spellService: SpellService
    constructor(spellService: SpellService) {
        this.spellService = spellService
    }

    actual abstract fun getAllSpells(): Flow<List<Spell>>
    actual abstract fun getHomebrewSpells(): Flow<List<Spell>>
    actual abstract fun getLiveSpell(id: Int): Flow<Spell>
    actual suspend fun insertSpell(spell: Spell): Int {
        return spellService.insertSpell(spell)
    }

    actual abstract fun getSpellClasses(id: Int): Flow<List<NameAndIdPojo>>
    actual suspend fun removeClassSpellCrossRef(classId: Int, spellId: Int) {
        spellService.removeClassSpellCrossRef(classId, spellId)
    }

    actual suspend fun addClassSpellCrossRef(classId: Int, spellId: Int) {
        spellService.addClassSpellCrossRef(classId, spellId)
    }

    actual abstract suspend fun removeSpellById(id: Int)
}


class SpellDaoImpl(service: SpellService) : SpellDao(service) {
    override fun getAllSpells(): Flow<List<Spell>> {
        return spellService.getAllSpells()
    }

    override fun getHomebrewSpells(): Flow<List<Spell>> {
        return spellService.getHomebrewSpells()
    }

    override fun getLiveSpell(id: Int): Flow<Spell> {
        return spellService.getLiveSpell(id)
    }

    override fun getSpellClasses(id: Int): Flow<List<NameAndIdPojo>> {
        return spellService.getSpellClasses(id)
    }

    override suspend fun removeSpellById(id: Int) {
        spellService.removeSpellById(id)
    }

}