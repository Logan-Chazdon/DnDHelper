package services

import io.ktor.client.*
import kotlinx.coroutines.flow.Flow
import model.Spell
import model.pojos.NameAndIdPojo

class SpellService(client: HttpClient) : Service(client = client) {
    fun insertSpell(spell: Spell): Int {
        TODO("Not yet implemented")
    }

    fun removeClassSpellCrossRef(classId: Int, spellId: Int) {
        TODO("Not yet implemented")
    }

    fun addClassSpellCrossRef(classId: Int, spellId: Int) {
        TODO("Not yet implemented")
    }

    fun getAllSpells(): Flow<List<Spell>> {
        TODO("Not yet implemented")
    }

    fun getHomebrewSpells(): Flow<List<Spell>> {
        TODO("Not yet implemented")
    }

    fun getLiveSpell(id: Int): Flow<Spell> {
        TODO("Not yet implemented")
    }

    fun getSpellClasses(id: Int): Flow<List<NameAndIdPojo>> {
        TODO("Not yet implemented")
    }

    fun removeSpellById(id: Int) {
        TODO("Not yet implemented")
    }
}