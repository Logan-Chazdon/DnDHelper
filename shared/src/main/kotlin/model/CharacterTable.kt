package model

import androidx.room.Embedded
import androidx.room.Ignore

class CharacterTable: Character() {
    @Embedded(prefix = "background")
    override var background: Background? = null


    override var race: Race?
        get() {
            return raceTable
        }
        set(value) { raceTable = value as RaceTable? }


    @Embedded
    var raceTable: RaceTable? = null

    @Ignore
    override var classes: MutableMap<String, Class> = mutableMapOf()
}