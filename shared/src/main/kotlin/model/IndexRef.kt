package model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class IndexRef(
    @PrimaryKey
    val index: String,
    val ids : List<Int>
)
