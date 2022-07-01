package gmail.loganchazdon.dndhelper.ui.navigation


import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector

data class NavItem(
    val name: String,
    val route: String,
    val baseRoute: String,
    val icon: ImageVector? = null,
    val painter: Painter? = null,
    val badgeCount: Int = 0
)

