package model.stateEntities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE


@Entity(
    primaryKeys = ["characterId", "featureId"],
    foreignKeys = [
        ForeignKey(
            entity = model.CharacterEntity::class,
            childColumns = ["characterId"],
            parentColumns = ["id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        ),
        ForeignKey(
            entity = model.FeatureEntity::class,
            childColumns = ["featureId"],
            parentColumns = ["featureId"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ]
)
data class CharacterFeatureState(
    val characterId: Int,
    val featureId : Int,
    val isActive: Boolean
)
