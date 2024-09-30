package model.database.daos

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import model.ClassSpellCrossRef
import model.Spell
import model.SpellTable
import model.asTable
import model.pojos.NameAndIdPojo

@Dao
actual abstract class SpellDao {

    actual fun insertSpell(spell: Spell): Int {
        val id = insertSpellOrIgnore(spell.asTable()).toInt()
        if (id == -1) {
            updateSpell(spell.asTable())
            return spell.id
        }
        return id
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract fun insertSpellOrIgnore(spell: SpellTable): Long

    @Update
    protected abstract fun updateSpell(spell: SpellTable)

    @Query("DELETE FROM spells WHERE id IS :id")
    actual abstract fun removeSpellById(id: Int)

    @Query("SELECT * FROM spells")
    actual abstract fun getAllSpells(): Flow<List<Spell>>

    @Query("SELECT id FROM spells WHERE LOWER(spells.name) LIKE LOWER(:name)")
    abstract fun getSpellIdByName(name: String): Int

    @Query("SELECT * FROM spells WHERE id IS :id")
    actual abstract fun getLiveSpell(id: Int): Flow<Spell>

    @Query(
        """
SELECT classes.name, classes.id  FROM classes
JOIN ClassSpellCrossRef ON ClassSpellCrossRef.classId IS classes.id
WHERE ClassSpellCrossRef.spellId = :id"""
    )
    actual abstract fun getSpellClasses(id: Int): Flow<List<NameAndIdPojo>>

    @Delete
    abstract fun removeClassSpellCrossRef(ref: ClassSpellCrossRef)
    actual fun removeClassSpellCrossRef(classId: Int, spellId: Int) {
        removeClassSpellCrossRef(
            ClassSpellCrossRef(
                classId = classId,
                spellId = spellId
            )
        )
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun addClassSpellCrossRef(ref: ClassSpellCrossRef)
    actual fun addClassSpellCrossRef(classId: Int, spellId: Int) {
        addClassSpellCrossRef(
            ClassSpellCrossRef(
                classId = classId,
                spellId = spellId
            )
        )
    }

    @Query("SELECT * FROM spells WHERE isHomebrew = 1")
    actual abstract fun getHomebrewSpells(): Flow<List<Spell>>
}