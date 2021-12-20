package com.example.dndhelper.ui.navigation


import androidx.compose.ui.graphics.vector.ImageVector

data class NavItem(
    val name: String,
    val route: String,
    val baseRoute: String,
    val icon: ImageVector,
    val badgeCount: Int = 0
)

