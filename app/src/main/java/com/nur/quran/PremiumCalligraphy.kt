package com.nur.quran

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * The fixed Arabic inscriptions are rendered using the bundled Amiri Quran font.
 * Only the decorative rosette slowly rotates; the sacred lettering remains still.
 * No remote images, tracking libraries or extra permissions are involved.
 */
@Composable
internal fun AnimatedCalligraphySeal(
    inscription: String,
    modifier: Modifier = Modifier,
    sizeDp: Int = 79
) {
    val transition = rememberInfiniteTransition(label = "calligraphy-decoration")
    val rotation by transition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(28000, easing = LinearEasing)),
        label = "rosette-turn"
    )
    val glow by transition.animateFloat(
        initialValue = .74f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3100), RepeatMode.Reverse),
        label = "soft-gold-light"
    )
    Box(
        modifier = modifier.size(sizeDp.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension * .49f
            drawCircle(Color(0xFFEFDE9F), radius, center)
            drawCircle(Color(0xFF055949), radius * .93f, center)
            drawCircle(Color(0xFFFFF8E8), radius * .80f, center)
            // Rotating eight-lobed arabesque: gold and turquoise, text never rotates.
            for (i in 0 until 8) {
                val a = (rotation + i * 45f) * PI / 180.0
                val xx = center.x + cos(a).toFloat() * radius * .73f
                val yy = center.y + sin(a).toFloat() * radius * .73f
                drawCircle(
                    if (i % 2 == 0) Color(0xFF0D9E89) else Color(0xFFEBC16E),
                    radius * .13f, Offset(xx, yy), alpha = glow
                )
            }
            drawCircle(Color(0xFFFFF9EC), radius * .64f, center)
            drawCircle(Color(0xFFD4A248), radius * .62f, center,
                style = Stroke(width = 2.dp.toPx()))
            drawCircle(Color(0xFF095F4B), radius * .95f, center,
                style = Stroke(width = 1.dp.toPx()))
        }
        Text(
            inscription,
            color = Color(0xFF005643), fontFamily = RefFont,
            fontSize = if (inscription == "محمد") 27.sp else 29.sp,
            lineHeight = 43.sp, maxLines = 1,
            style = TextStyle(textDirection = TextDirection.Rtl),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
internal fun PremiumBasmalahHeader(surah: Surah) {
    val green = Color(0xFF075346)
    val gold = Color(0xFFD4A248)
    androidx.compose.material3.Surface(
        color = Color(0xFFFFF8E9),
        border = androidx.compose.foundation.BorderStroke(1.dp, gold),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 7.dp, vertical = 4.dp)
    ) {
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AnimatedCalligraphySeal("الله", sizeDp = 77)
                Column(Modifier.weight(1f).padding(horizontal = 3.dp),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(surah.arabicName, color = green, fontFamily = RefFont,
                        fontSize = 29.sp, lineHeight = 45.sp, maxLines = 1,
                        style = TextStyle(textDirection = TextDirection.Rtl),
                        textAlign = TextAlign.Center)
                    Text(surah.turkishName.uppercase() + " SURESİ",
                        color = green, fontSize = 10.sp, letterSpacing = 1.sp,
                        textAlign = TextAlign.Center)
                }
                AnimatedCalligraphySeal("محمد", sizeDp = 77)
            }
            if (surah.openingBasmala != null) {
                Spacer(Modifier.height(7.dp))
                Text(
                    surah.openingBasmala,
                    fontFamily = RefFont, color = green,
                    fontSize = 27.sp, lineHeight = 48.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    style = TextStyle(textDirection = TextDirection.Rtl)
                )
            }
            Spacer(Modifier.height(5.dp))
            Canvas(Modifier.fillMaxWidth().height(8.dp)) {
                drawLine(gold, Offset(0f, size.height / 2), Offset(size.width, size.height / 2),
                    strokeWidth = 1.7.dp.toPx())
                drawCircle(green, size.height * .39f, Offset(size.width / 2, size.height / 2))
                drawCircle(gold, size.height * .25f, Offset(size.width / 2, size.height / 2))
            }
        }
    }
}
