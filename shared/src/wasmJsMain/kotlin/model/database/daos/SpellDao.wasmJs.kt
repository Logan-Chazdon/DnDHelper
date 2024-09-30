package model.database.daos

import kotlinx.coroutines.flow.Flow
import model.Spell
import model.pojos.NameAndIdPojo

actual abstract class SpellDao {
    actual abstract fun getAllSpells(): Flow<List<Spell>>
    actual abstract fun getHomebrewSpells(): Flow<List<Spell>>
    actual abstract fun getLiveSpell(id: Int): Flow<Spell>
    actual fun insertSpell(spell: Spell): Int {
        TODO("Not yet implemented")
    }

    actual abstract fun getSpellClasses(id: Int): Flow<List<NameAndIdPojo>>
    actual fun removeClassSpellCrossRef(classId: Int, spellId: Int) {
    }

    actual fun addClassSpellCrossRef(classId: Int, spellId: Int) {
    }

    actual abstract fun removeSpellById(id: Int)
}


class SpellDaoImpl() : SpellDao() {
    override fun getAllSpells(): Flow<List<Spell>> {
        TODO("Not yet implemented")
    }

    override fun getHomebrewSpells(): Flow<List<Spell>> {
        TODO("Not yet implemented")
    }

    override fun getLiveSpell(id: Int): Flow<Spell> {
        TODO("Not yet implemented")
    }

    override fun getSpellClasses(id: Int): Flow<List<NameAndIdPojo>> {
        TODO("Not yet implemented")
    }

    override fun removeSpellById(id: Int) {
        TODO("Not yet implemented")
    }

}