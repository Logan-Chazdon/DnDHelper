package model

import androidx.room.Embedded

class RaceTable(
    @Embedded(prefix = "subrace")
    override var subrace: Subrace? = null
) : Race(
    subrace = subrace
) {

}