package gmail.loganchazdon.dndhelper.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import gmail.loganchazdon.dndhelper.model.database.Converters

@Entity(tableName = "classes")
@TypeConverters(Converters::class)
open class ClassEntity(
    open var name: String,
    open var hitDie: Int = 8,
    open var subclassLevel: Int,
    open var proficiencyChoices: List<ProficiencyChoice> = emptyList(),
    open var proficiencies: List<Proficiency> = emptyList(),
    open var equipmentChoices: List<ItemChoice> = emptyList(),
    open var equipment: List<ItemInterface> = emptyList(),
    open val spellCasting : SpellCasting? = null,
    open val pactMagic: PactMagic? = null,
    open val startingGoldD4s: Int,
    open val startingGoldMultiplier : Int = 10,
    @PrimaryKey(autoGenerate = true)
    open var id: Int = 0,
    var isHomebrew : Boolean = false
)