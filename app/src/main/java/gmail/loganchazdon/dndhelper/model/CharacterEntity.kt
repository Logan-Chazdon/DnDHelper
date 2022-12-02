package gmail.loganchazdon.dndhelper.model


import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import gmail.loganchazdon.dndhelper.model.database.Converters

@Entity(tableName="characters")
@TypeConverters(Converters::class)
open class CharacterEntity (
    open var name: String = "",
    open var personalityTraits: String = "",
    open var ideals: String = "",
    open var bonds: String = "",
    open var flaws: String = "",
    open var notes: String = "",
    open var currentHp: Int = 0,
    open var tempHp: Int = 0,
    open var conditions: MutableList<String> = mutableListOf<String>(),
    open var resistances: MutableList<String> = mutableListOf<String>(),
    @PrimaryKey(autoGenerate = true)
    open var id: Int = 0,
    open var statGenerationMethodIndex: Int = 0,
    open var baseStats: MutableMap<String, Int> = mutableMapOf<String, Int>(),
    open var backpack: Backpack = Backpack(),
    open var inspiration: Boolean = false,
    open var positiveDeathSaves: Int = 0,
    open var negativeDeathSaves: Int = 0,
    open var spellSlots: List<Resource> = listOf(),
    open var addedLanguages: MutableList<Language> = mutableListOf<Language>(),
    open var addedProficiencies: MutableList<Proficiency> = mutableListOf<Proficiency>()
)