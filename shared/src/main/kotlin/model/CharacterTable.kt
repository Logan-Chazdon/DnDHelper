package model

import androidx.room.Embedded
import androidx.room.Ignore

class CharacterTable: Character() {
    @Embedded(prefix = "background")
    override var background: Background? = null
    @Embedded
    override var race: Race? = null
    @Ignore
    override var classes: MutableMap<String, Class> = mutableMapOf()
}