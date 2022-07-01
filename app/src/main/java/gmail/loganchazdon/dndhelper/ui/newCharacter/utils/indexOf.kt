package gmail.loganchazdon.dndhelper.ui.newCharacter.utils

//Function to get a specific occurrence by index.
fun <E> List<E>.indexOf(element: E, num: Int): Int? {
    var numFound = 0
    this.forEachIndexed { index, it ->
        if(it == element) {
            numFound++
        }
        if(numFound == num) {
            return index
        }
    }
    return null
}