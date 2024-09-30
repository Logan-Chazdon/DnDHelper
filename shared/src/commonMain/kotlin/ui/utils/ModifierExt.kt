package ui.utils

import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import ui.platformSpecific.getScreenHeight
import ui.platformSpecific.getScreenWidth

fun Modifier.mediaQuery(
    comparator: Dimensions.DimensionComparator,
    modifier: Modifier
) : Modifier = composed {
    val screenWidth = getScreenWidth()
    val screenHeight = getScreenHeight()
    if(comparator.compare(screenWidth, screenHeight)) {
        this.then(modifier)
    } else this
}