package model.repositories


import kotlinx.coroutines.flow.Flow
import model.Spell
import model.junctionEntities.ClassSpellCrossRef
import model.pojos.NameAndIdPojo

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect class SpellRepository {
    fun getLiveSpells(): Flow<List<Spell>>
    fun getHomebrewSpells(): Flow<List<Spell>>
    fun getAllSpells(): List<Spell>
    fun getLiveSpell(id: Int): Flow<Spell>
    fun insertSpell(spell: Spell)
    fun createDefaultSpell(): Int
    fun getSpellClasses(id: Int): Flow<List<NameAndIdPojo>>
    fun removeClassSpellCrossRef(ref: ClassSpellCrossRef)
    fun addClassSpellCrossRef(ref: ClassSpellCrossRef)
    fun deleteSpell(id: Int)

    companion object {
        val allSpellLevels: List<Pair<Int, String>>
    }
}