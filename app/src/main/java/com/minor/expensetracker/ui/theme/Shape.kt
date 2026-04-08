package com.minor.expensetracker.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

// Custom shape tokens
val CardShape = RoundedCornerShape(20.dp)
val BottomSheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
val ButtonShape = RoundedCornerShape(14.dp)
val InputFieldShape = RoundedCornerShape(14.dp)
val ChipShape = RoundedCornerShape(24.dp)
val IconContainerShape = RoundedCornerShape(14.dp)
val BottomNavShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
