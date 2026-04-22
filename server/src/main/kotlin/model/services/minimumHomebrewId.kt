package gmail.loganchazdon.dndhelper.model.services

import kotlin.math.max


/** The lowest id we assign to any homebrew content to allow space for vanilla content*/
private const val minimumHomebrewId = 10000L

/**
 * Returns the maximum of the passed id or the minium allowable homebrew id to reserve space for vanilla content.
 */
fun Long.orMinimum() : Long {
    return max(this, minimumHomebrewId)
}