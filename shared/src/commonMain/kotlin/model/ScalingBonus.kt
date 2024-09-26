package model


class ScalingBonus(
    //Maps the num required to the output
    private val bonuses : Map<Int, Int>
) {
    //Find and return the highest output for which the num required is less than or equal to num.
    fun calculate(num: Int): Int {
        var result = 0
        bonuses.forEach {
            if(it.key <= num && it.value > result) {
                result = it.value
            }
        }
        return result
    }
}