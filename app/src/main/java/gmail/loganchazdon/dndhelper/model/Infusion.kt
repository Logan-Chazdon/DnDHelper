package gmail.loganchazdon.dndhelper.model

data class Infusion(
    val maxTimesChosen : Int? = null, //Passed on to whatever feature grants the infusion.
    val options: List<Feature>? = null,
    //If the infusion lets us choose other infusions.
    //Eg replicated magic item. Wrap the infusions in features
    //and put them in this.
    val choose: Int = 0, //Used for feature wrapping.
    val name: String,
    val desc: String,
    val targetItemFilter: TargetItemFilter?,
    val charges: Resource? = null,
    val acBonus: ScalingBonus? = null,
    val atkDmgBonus: ScalingBonus? = null,
    var active: Boolean = false,
    val attuned: Boolean,
    var level: Int = 0,
    val grantedAtLevel: Int
) {
    val currentAcBonus: Int?
    get() {
        return acBonus?.calculate(level)
    }

    val currentAtkDmgBonus: Int?
    get() {
        return atkDmgBonus?.calculate(level)
    }
}
