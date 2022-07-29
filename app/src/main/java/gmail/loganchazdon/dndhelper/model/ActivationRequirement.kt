package gmail.loganchazdon.dndhelper.model

//A class used to determine if a features benefits should be applied.
data class ActivationRequirement(
    val armorReqIndex: String? = null,
    val shieldReq: Boolean? = null
) {
    fun checkActivation(
        character: Character
    ) : Boolean {
        armorReqIndex?.let {
            if(it != character.backpack.equippedArmor.index) {
                return false
            }
        }
        shieldReq?.let {
            if(it != (character.backpack.equippedShield != null)) {
                return false
            }
        }

        return true
    }
}