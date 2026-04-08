package com.minor.expensetracker.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme

fun Modifier.glassEffect(
    shape: Shape = RoundedCornerShape(20.dp),
    borderWidth: Dp = 1.dp,
    alphaLight: Float = 0.1f,
    alphaDark: Float = 0.02f
): Modifier = composed {
    val isDarkTheme = com.minor.expensetracker.ui.theme.ThemeState.isDarkMode

    if (isDarkTheme) {
        // Dark mode: classic glassmorphism — faint white fill + white edge border
        this
            .clip(shape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = alphaLight),
                        Color.White.copy(alpha = alphaDark)
                    ),
                    start = Offset.Zero,
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                ),
                shape = shape
            )
            .border(
                width = borderWidth,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.25f),
                        Color.White.copy(alpha = 0.01f),
                        Color.White.copy(alpha = 0.05f)
                    ),
                    start = Offset.Zero,
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                ),
                shape = shape
            )
    } else {
        // Light mode: solid white card with subtle shadow-like border — readable on light bg
        this
            .clip(shape)
            .background(
                color = Color.White,
                shape = shape
            )
            .border(
                width = borderWidth,
                color = Color(0xFFE2E8F0), // cool-grey border, clearly visible on light bg
                shape = shape
            )
    }
}

@Composable
fun AnimatedMeshBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mesh")
    val offX by infiniteTransition.animateFloat(
        initialValue = -200f,
        targetValue = 400f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "x"
    )

    val offY by infiniteTransition.animateFloat(
        initialValue = -100f,
        targetValue = 600f,
        animationSpec = infiniteRepeatable(
            animation = tween(22000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "y"
    )

    val isDark = com.minor.expensetracker.ui.theme.ThemeState.isDarkMode
    val bgColor = if (isDark) Color(0xFF08080A) else Color(0xFFF4F6FB)
    val tealColor = if (isDark) Color(0xFF10463D).copy(alpha = 0.4f) else Color(0xFF99F6E4).copy(alpha = 0.35f)
    val purpleColor = if (isDark) Color(0xFF33146B).copy(alpha = 0.4f) else Color(0xFFDDD6FE).copy(alpha = 0.35f)

    Box(modifier = modifier.fillMaxSize().background(bgColor)) {
        // Soft Teal glow
        Box(
            modifier = Modifier
                .requiredSize(800.dp)
                .offset(x = (offX - 200).dp, y = (offY - 200).dp)
                .background(tealColor, shape = RoundedCornerShape(400.dp))
                .blur(radius = 160.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
        )
        
        // Soft Purple glow moving opposite
        Box(
            modifier = Modifier
                .requiredSize(700.dp)
                .offset(x = (400 - offX).dp, y = (offY * 0.4f).dp)
                .background(purpleColor, shape = RoundedCornerShape(350.dp))
                .blur(radius = 180.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
        )

        content()
    }
}
