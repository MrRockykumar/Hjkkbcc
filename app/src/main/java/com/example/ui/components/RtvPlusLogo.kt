package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RtvPlusLogo(
    modifier: Modifier = Modifier,
    logoHeight: Dp = 20.dp,
    showBackground: Boolean = true
) {
    // Custom slanted capsule shape to replicate the glossy background card
    val slantedShape = GenericShape { size, _ ->
        // Left height is slightly larger, slanted from left to right
        val leftHeight = size.height
        val rightHeight = size.height * 0.85f
        val topOffset = size.height * 0.05f
        
        moveTo(0f, leftHeight * 0.15f)
        lineTo(size.width * 0.8f, topOffset)
        quadraticTo(size.width, topOffset, size.width, rightHeight * 0.25f)
        lineTo(size.width, rightHeight * 0.85f)
        quadraticTo(size.width, rightHeight, size.width * 0.8f, rightHeight)
        lineTo(size.width * 0.15f, leftHeight)
        quadraticTo(0f, leftHeight, 0f, leftHeight * 0.85f)
        close()
    }

    Box(
        modifier = modifier
            .height(logoHeight + 14.dp)
            .widthIn(min = 84.dp)
            .let {
                if (showBackground) {
                    it
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFFFFFFFF), Color(0xFFF0F2F5))
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = Color(0xFFD4D9E2),
                            shape = RoundedCornerShape(10.dp)
                        )
                } else {
                    it
                }
            }
            .padding(horizontal = 10.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(bottom = 1.dp)
        ) {
            // Bold styled Red 'R' matching the brand
            Text(
                text = "R",
                color = Color(0xFFE50914), // Vibrant Brand Red
                fontWeight = FontWeight.Black,
                fontSize = (logoHeight.value * 1.1f).sp,
                style = LocalTextStyle.current.copy(
                    fontStyle = FontStyle.Normal,
                    shadow = Shadow(color = Color(0x22E50914), offset = Offset(1f, 1f), blurRadius = 2f)
                )
            )
            // Silver-charcoal 'TV' matching the original logo lettering
            Text(
                text = "TV",
                color = Color(0xFF1E293B), // Charcoal
                fontWeight = FontWeight.ExtraBold,
                fontSize = (logoHeight.value * 0.95f).sp,
                style = LocalTextStyle.current.copy(
                    fontStyle = FontStyle.Italic
                ),
                modifier = Modifier.padding(start = 1.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            // Neon Blue '+' matching the glowing blue plus
            Text(
                text = "+",
                color = Color(0xFF007BFF), // Vibrant Blue
                fontWeight = FontWeight.Black,
                fontSize = (logoHeight.value * 1.15f).sp,
                style = LocalTextStyle.current.copy(
                    shadow = Shadow(color = Color(0x55007BFF), offset = Offset(1f, 1f), blurRadius = 3f)
                )
            )
        }
    }
}
