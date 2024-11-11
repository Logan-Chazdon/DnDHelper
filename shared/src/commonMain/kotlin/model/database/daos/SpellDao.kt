package model.database.daos

import kotlinx.coroutines.flow.Flow
import model.Spell
import model.pojos.NameAndIdPojo

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect abstract class SpellDao {
    abstract fun getAllSpells(): Flow<List<Spell>>
    abstract fun getHomebrewSpells(): Flow<List<Spell>>
    abstract fun getLiveSpell(id: Int): Flow<Spell>
    suspend fun insertSpell(spell: Spell) : Int
    abstract fun getSpellClasses(id: Int): Flow<List<NameAndIdPojo>>
    suspend fun removeClassSpellCrossRef(classId: Int, spellId: Int)
    suspend fun addClassSpellCrossRef(classId: Int, spellId: Int)
    abstract suspend fun removeSpellById(id: Int)
}