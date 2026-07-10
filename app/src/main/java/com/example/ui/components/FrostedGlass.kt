package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Dark Frosted Glass Color Theme
object GlassTheme {
    val BackgroundDark = Color(0xFF09090B)
    val GlassCardBg = Color(0x12FFFFFF) // 7% White for the frosted glass overlay
    val GlassCardBgActive = Color(0x22FFFFFF) // 13% White when pressed
    val GlassBorder = Color(0x22FFFFFF) // White border with low opacity
    val PrimaryGlow = Color(0x406366F1) // Indigo transparent glow
    val SecondaryGlow = Color(0x30EC4899) // Pink transparent glow
    val TextWhite = Color(0xFFFAFAFA)
    val TextMuted = Color(0xFF94A3B8)
    val AccentColor = Color(0xFF6366F1) // Indigo-500
    val AccentPink = Color(0xFFEC4899) // Pink-500
}

@Composable
fun GlassBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    // Pulse-like movement for background blobs to make it feel alive and emotional
    val infiniteTransition = rememberInfiniteTransition(label = "BgPulse")
    
    val pulseOffset1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 60f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Blob1"
    )

    val pulseOffset2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -50f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Blob2"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(GlassTheme.BackgroundDark)
    ) {
        // Glowing mesh gradient blobs to shine through the glass layer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawWithContent {
                    // Blob 1: Indigo Top-Right
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(GlassTheme.PrimaryGlow, Color.Transparent),
                            center = Offset(size.width * 0.8f + pulseOffset1, size.height * 0.2f)
                        ),
                        radius = size.width * 0.6f
                    )

                    // Blob 2: Pink Bottom-Left
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(GlassTheme.SecondaryGlow, Color.Transparent),
                            center = Offset(size.width * 0.1f + pulseOffset2, size.height * 0.8f)
                        ),
                        radius = size.width * 0.7f
                    )
                }
                .blur(80.dp) // Dynamic background blurring to blend the mesh shapes
        )
        
        Box(modifier = Modifier.fillMaxSize()) {
            content()
        }
    }
}

@Composable
fun FrostedGlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    borderWidth: Dp = 1.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(GlassTheme.GlassCardBg)
            .border(
                width = borderWidth,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0x33FFFFFF), // Brighter at top left
                        Color(0x05FFFFFF)  // Darker at bottom right
                    )
                ),
                shape = shape
            )
    ) {
        content()
    }
}
