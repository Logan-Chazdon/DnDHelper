package model.database.daos

import kotlinx.coroutines.flow.Flow
import model.Spell
import model.pojos.NameAndIdPojo

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect abstract class SpellDao {
    abstract fun getAllSpells(): Flow<List<Spell>>
    abstract fun getHomebrewSpells(): Flow<List<Spell>>
    abstract fun getLiveSpell(id: Int): Flow<Spell>
    fun insertSpell(spell: Spell) : Int
    abstract fun getSpellClasses(id: Int): Flow<List<NameAndIdPojo>>
    fun removeClassSpellCrossRef(classId: Int, spellId: Int)
    fun addClassSpellCrossRef(classId: Int, spellId: Int)
    abstract fun removeSpellById(id: Int)
}