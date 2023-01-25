package gmail.loganchazdon.dndhelper.model.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import gmail.loganchazdon.dndhelper.model.Spell

@Dao
abstract class SpellDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertSpell(spell: Spell): Long

    @Query("DELETE FROM spells WHERE id IS :id")
    abstract fun removeSpellById(id: Int)

    @Query("SELECT * FROM spells")
    abstract fun getAllSpells(): LiveData<List<Spell>>

    @Query("SELECT id FROM spells WHERE LOWER(spells.name) LIKE LOWER(:name)")
    abstract fun getSpellIdByName(name: String): Int
}