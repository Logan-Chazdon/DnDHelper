package gmail.loganchazdon.dndhelper.model.repositories

import androidx.lifecycle.LiveData
import gmail.loganchazdon.dndhelper.model.Spell
import gmail.loganchazdon.dndhelper.model.database.daos.SpellDao
import gmail.loganchazdon.dndhelper.model.junctionEntities.ClassSpellCrossRef
import gmail.loganchazdon.dndhelper.model.pojos.NameAndIdPojo
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

    fun getLiveSpell(id: Int): LiveData<Spell> {
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
        ).toInt()
    }

    fun getSpellClasses(id: Int): LiveData<List<NameAndIdPojo>> {
        return spellDao.getSpellClasses(id)
    }

    fun removeClassSpellCrossRef(ref: ClassSpellCrossRef) {
        spellDao.removeClassSpellCrossRef(ref)
    }

    fun addClassSpellCrossRef(ref: ClassSpellCrossRef) {
        spellDao.addClassSpellCrossRef(ref)
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