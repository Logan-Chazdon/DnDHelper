package gmail.loganchazdon.dndhelper.model.choiceEntities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import gmail.loganchazdon.dndhelper.model.AbilityBonus
import gmail.loganchazdon.dndhelper.model.CharacterEntity
import gmail.loganchazdon.dndhelper.model.SubraceEntity


@Entity(
    primaryKeys = ["subraceId", "characterId"],
    foreignKeys = [
        ForeignKey(
            entity = SubraceEntity::class,
            childColumns = ["subraceId"],
            parentColumns = ["id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        ),
        ForeignKey(
            entity = CharacterEntity::class,
            childColumns = ["characterId"],
            parentColumns = ["id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ]
)
data class SubraceChoiceEntity(
    val subraceId : Int,
    val characterId: Int,
    var languageChoice: List<List<String>> = emptyList(),
    @ColumnInfo(name = "abcchosenByString")
    var abilityBonusChoice: List<String> = emptyList(),
    var abilityBonusOverrides: List<AbilityBonus>? = null
)
