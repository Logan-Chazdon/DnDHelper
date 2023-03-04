package gmail.loganchazdon.dndhelper.model.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import gmail.loganchazdon.dndhelper.model.Spell
import gmail.loganchazdon.dndhelper.model.junctionEntities.ClassSpellCrossRef
import gmail.loganchazdon.dndhelper.model.pojos.NameAndIdPojo

@Dao
abstract class SpellDao {

    fun insertSpell(spell: Spell): Int {
        val id = insertSpellOrIgnore(spell).toInt()
        if(id == -1) {
            updateSpell(spell)
            return spell.id
        }
        return id
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract fun insertSpellOrIgnore(spell: Spell): Long

    @Update
    protected abstract fun updateSpell(spell: Spell)

    @Query("DELETE FROM spells WHERE id IS :id")
    abstract fun removeSpellById(id: Int)

    @Query("SELECT * FROM spells")
    abstract fun getAllSpells(): LiveData<List<Spell>>

    @Query("SELECT id FROM spells WHERE LOWER(spells.name) LIKE LOWER(:name)")
    abstract fun getSpellIdByName(name: String): Int

    @Query("SELECT * FROM spells WHERE id IS :id")
    abstract fun getLiveSpell(id: Int): LiveData<Spell>

    @Query("""
SELECT classes.name, classes.id  FROM classes
JOIN ClassSpellCrossRef ON ClassSpellCrossRef.classId IS classes.id
WHERE ClassSpellCrossRef.spellId = :id""")
    abstract fun getSpellClasses(id: Int): LiveData<List<NameAndIdPojo>>

    @Delete
    abstract fun removeClassSpellCrossRef(ref: ClassSpellCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun addClassSpellCrossRef(ref: ClassSpellCrossRef)

    @Query("SELECT * FROM spells WHERE isHomebrew = 1")
    abstract fun getHomebrewSpells(): LiveData<List<Spell>>
}