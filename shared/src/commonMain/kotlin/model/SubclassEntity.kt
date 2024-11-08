package model

import kotlinx.serialization.Serializable
import model.converters.BooleanAsIntSerializer

@Serializable
open class SubclassEntity(
    open var name: String,
    var spellAreFree: Boolean,
    open var spellCasting: SpellCasting?,
    @Serializable(with = BooleanAsIntSerializer::class)
    open var isHomebrew : Boolean = false
) {
    open var subclassId : Int = 0
}
