package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CityLocation
import com.example.data.KnownCities
import com.example.ui.theme.AppSkin
import com.example.ui.theme.getSkinChatColors

@OptIn(ExperimentalTextApi::class)
@Composable
fun InteractiveMapCanvas(
    startCityName: String,
    endCityName: String,
    travelMode: String,
    activeSkin: AppSkin,
    onMapLocationTapped: (CityLocation) -> Unit,
    modifier: Modifier = Modifier
) {
    var zoomScale by remember { mutableFloatStateOf(1.0f) }
    var panOffset by remember { mutableStateOf(Offset.Zero) }

    val skinChatColors = getSkinChatColors(activeSkin)
    val textMeasurer = rememberTextMeasurer()

    // Route pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "routePulse")
    val pulsePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulsePhase"
    )

    val startCity = remember(startCityName) { KnownCities.findCity(startCityName) ?: KnownCities.CITIES[0] }
    val endCity = remember(endCityName) { KnownCities.findCity(endCityName) ?: KnownCities.CITIES[1] }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(230.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(skinChatColors.mapBg)
            .border(1.dp, skinChatColors.mapGridLine, RoundedCornerShape(16.dp))
            .testTag("interactive_map_canvas")
    ) {
        // Map Canvas Drawing
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        zoomScale = (zoomScale * zoom).coerceIn(0.6f, 2.5f)
                        panOffset += pan
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures { tapOffset ->
                        val center = Offset(size.width / 2f, size.height / 2f) + panOffset
                        val tappedCity = KnownCities.CITIES.minByOrNull { city ->
                            val x = center.x + (city.lng * 2.2f * zoomScale)
                            val y = center.y - (city.lat * 2.2f * zoomScale)
                            val cityOffset = Offset(x.toFloat(), y.toFloat())
                            (cityOffset - tapOffset).getDistanceSquared()
                        }
                        tappedCity?.let { onMapLocationTapped(it) }
                    }
                }
        ) {
            val width = size.width
            val height = size.height
            val center = Offset(width / 2f, height / 2f) + panOffset

            // 1. Draw Map Grid
            val gridSize = 40.dp.toPx() * zoomScale
            var x = (center.x % gridSize)
            while (x < width) {
                drawLine(
                    color = skinChatColors.mapGridLine,
                    start = Offset(x, 0f),
                    end = Offset(x, height),
                    strokeWidth = 1f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
                )
                x += gridSize
            }

            var y = (center.y % gridSize)
            while (y < height) {
                drawLine(
                    color = skinChatColors.mapGridLine,
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
                )
                y += gridSize
            }

            // 2. Draw Known Cities
            KnownCities.CITIES.forEach { city ->
                val cityX = center.x + (city.lng * 2.2f * zoomScale).toFloat()
                val cityY = center.y - (city.lat * 2.2f * zoomScale).toFloat()

                val isStart = city.name.equals(startCity.name, ignoreCase = true)
                val isEnd = city.name.equals(endCity.name, ignoreCase = true)

                if (!isStart && !isEnd) {
                    // Normal city node
                    drawCircle(
                        color = skinChatColors.mapGridLine.copy(alpha = 0.8f),
                        radius = 4.dp.toPx(),
                        center = Offset(cityX, cityY)
                    )
                    drawText(
                        textMeasurer = textMeasurer,
                        text = city.name,
                        topLeft = Offset(cityX + 6.dp.toPx(), cityY - 8.dp.toPx()),
                        style = TextStyle(
                            color = skinChatColors.bubbleReceivedText.copy(alpha = 0.5f),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Normal
                        )
                    )
                }
            }

            // 3. Convert Start and End Cities to Canvas Coordinates
            val startX = center.x + (startCity.lng * 2.2f * zoomScale).toFloat()
            val startY = center.y - (startCity.lat * 2.2f * zoomScale).toFloat()
            val endX = center.x + (endCity.lng * 2.2f * zoomScale).toFloat()
            val endY = center.y - (endCity.lat * 2.2f * zoomScale).toFloat()

            val startPos = Offset(startX, startY)
            val endPos = Offset(endX, endY)

            // 4. Draw Animated Curved Path between Start & End
            val path = Path()
            path.moveTo(startPos.x, startPos.y)

            // Calculate Bezier control point for curved route
            val midX = (startPos.x + endPos.x) / 2f
            val midY = (startPos.y + endPos.y) / 2f - 40.dp.toPx() * zoomScale
            path.quadraticTo(midX, midY, endPos.x, endPos.y)

            val routeColor = when (travelMode.lowercase()) {
                "boat" -> Color(0xFF00B0FF)
                "flight" -> Color(0xFFFF4081)
                else -> Color(0xFF00E676)
            }

            // Outer glow path
            drawPath(
                path = path,
                color = routeColor.copy(alpha = 0.3f),
                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
            )

            // Solid route line
            drawPath(
                path = path,
                color = routeColor,
                style = Stroke(
                    width = 3.dp.toPx(),
                    cap = StrokeCap.Round,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12.dp.toPx(), 8.dp.toPx()), pulsePhase * 20.dp.toPx())
                )
            )

            // 5. Animated Moving Vehicle Dot along curve
            val t = pulsePhase
            val animatedX = (1 - t) * (1 - t) * startPos.x + 2 * (1 - t) * t * midX + t * t * endPos.x
            val animatedY = (1 - t) * (1 - t) * startPos.y + 2 * (1 - t) * t * midY + t * t * endPos.y

            drawCircle(
                color = Color.White,
                radius = 6.dp.toPx(),
                center = Offset(animatedX, animatedY)
            )
            drawCircle(
                color = routeColor,
                radius = 4.dp.toPx(),
                center = Offset(animatedX, animatedY)
            )

            // 6. Draw Start Pin (Green/Cyan)
            drawCircle(
                color = Color(0xFF00E676),
                radius = 8.dp.toPx(),
                center = startPos
            )
            drawCircle(
                color = Color.White,
                radius = 3.dp.toPx(),
                center = startPos
            )
            drawText(
                textMeasurer = textMeasurer,
                text = "🚩 ${startCity.name}",
                topLeft = Offset(startPos.x - 20.dp.toPx(), startPos.y - 22.dp.toPx()),
                style = TextStyle(
                    color = routeColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    background = Color.Black.copy(alpha = 0.6f)
                )
            )

            // 7. Draw End Pin (Red/Magenta)
            drawCircle(
                color = Color(0xFFFF1744),
                radius = 8.dp.toPx(),
                center = endPos
            )
            drawCircle(
                color = Color.White,
                radius = 3.dp.toPx(),
                center = endPos
            )
            drawText(
                textMeasurer = textMeasurer,
                text = "📍 ${endCity.name}",
                topLeft = Offset(endPos.x - 20.dp.toPx(), endPos.y + 10.dp.toPx()),
                style = TextStyle(
                    color = Color(0xFFFF1744),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    background = Color.Black.copy(alpha = 0.6f)
                )
            )
        }

        // Overlay Map Controls (Zoom In/Out, Recenter, Scale)
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(
                onClick = { zoomScale = (zoomScale * 1.2f).coerceAtMost(2.5f) },
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                    .testTag("map_zoom_in_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Zoom In",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(18.dp)
                )
            }

            IconButton(
                onClick = { zoomScale = (zoomScale / 1.2f).coerceAtLeast(0.6f) },
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                    .testTag("map_zoom_out_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Zoom Out",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(18.dp)
                )
            }

            IconButton(
                onClick = {
                    zoomScale = 1.0f
                    panOffset = Offset.Zero
                },
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                    .testTag("map_recenter_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "Recenter Map",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Map Legend / Mode indicator overlay on bottom left
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val modeEmoji = when (travelMode.lowercase()) {
                    "boat" -> "🚤"
                    "flight" -> "✈️"
                    else -> "🚗"
                }
                Text(
                    text = "$modeEmoji Map Mode • Tap cities on map to select",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
