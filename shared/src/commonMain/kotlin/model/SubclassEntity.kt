package model

import kotlinx.serialization.Serializable
import model.converters.BooleanAsIntSerializer


open class SubclassEntity(
    open var name: String,
    val spellAreFree: Boolean,
    open var spellCasting: SpellCasting?,
    @Serializable(with = BooleanAsIntSerializer::class)
    open var isHomebrew : Boolean = false
) {
    open var subclassId : Int = 0
}
