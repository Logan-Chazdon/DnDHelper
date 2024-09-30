package model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE

/**This is used to fetch a race relating to a character.*/
@Entity(
    primaryKeys = ["raceId", "id"],
    foreignKeys = [
        ForeignKey(
            entity = RaceEntityTable::class,
            childColumns = ["raceId"],
            parentColumns = ["raceId"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        ),
        ForeignKey(
            entity = CharacterEntityTable::class,
            childColumns = ["id"],
            parentColumns = ["id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ]
)
data class CharacterRaceCrossRef(
    val raceId: Int,
    val id: Int
)