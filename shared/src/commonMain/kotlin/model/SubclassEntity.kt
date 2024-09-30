package model



open class SubclassEntity(
    open var name: String,
    val spellAreFree: Boolean,
    open var spellCasting: SpellCasting?,
    open var isHomebrew : Boolean = false
) {
    open var subclassId : Int = 0
}
