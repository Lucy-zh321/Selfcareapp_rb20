package com.example.selfcare

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldState.Saver.restore
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.IconButton
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalDensity
import kotlin.math.sqrt


@Composable
fun ColorCircle(
    color: Color,
    isSelected: Boolean,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(25.dp) // Smaller circle
            .clip(CircleShape)
            .border(
                2.dp, // Thicker border
                color,
                CircleShape
            )
            .background(Color.Transparent, CircleShape) // Always transparent background
            .clickable(onClick = onSelected),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            // Filled inner circle instead of checkmark
            Box(
                modifier = Modifier
                    .size(15.dp) // Inner circle size
                    .clip(CircleShape)
                    .background(color, CircleShape)
            )
        }
    }
}



@Composable
fun ColorCustomizationDialog(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    mode: String,
    onModeChanged: (String) -> Unit,
    customColors: List<Color>,
    onPresetRemoved: (Int) -> Unit,
    onPresetAdded: (Int, Color) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        title = { Text("Color Customization") },
        text = {
            Column {
                // Hexagon Grid with exactly 127 clickable hexagons
                // Hexagon Grid with exactly 127 clickable hexagons
                // Hexagon Grid with exactly 127 clickable hexagons
                // Hexagon Grid with exactly 127 clickable hexagons
                // Hexagon Grid with exactly 127 clickable hexagons
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp)
                        .background(Color.LightGray.copy(alpha = 0.1f))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val density = LocalDensity.current
                    val hexSize = 10.dp
                    val horizontalSpacingMultiplier = 1.55f // Perfect spacing with tiny gap
                    var selectedHexagonIndex by remember { mutableStateOf<Int?>(null) }

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val hexSizePx = hexSize.toPx()
                        val centerX = size.width / 2
                        val centerY = size.height / 2

                        // Create hexagon path centered at (0,0)
                        val hexagonPath = Path().apply {
                            for (i in 0..5) {
                                val angle = 2f * Math.PI.toFloat() / 6f * i - Math.PI.toFloat() / 6f
                                val x = cos(angle) * hexSizePx
                                val y = sin(angle) * hexSizePx
                                if (i == 0) moveTo(x, y) else lineTo(x, y)
                            }
                            close()
                        }

                        // Create larger hexagon path for selected state
                        val selectedHexagonPath = Path().apply {
                            for (i in 0..5) {
                                val angle = 2f * Math.PI.toFloat() / 6f * i - Math.PI.toFloat() / 6f
                                val x = cos(angle) * (hexSizePx * 1.15f) // 15% larger
                                val y = sin(angle) * (hexSizePx * 1.15f) // 15% larger
                                if (i == 0) moveTo(x, y) else lineTo(x, y)
                            }
                            close()
                        }

                        // Row distribution for exactly 127 hexagons: 7,8,9,10,11,12,13,12,11,10,9,8,7
                        val rowCounts = listOf(7, 8, 9, 10, 11, 12, 13, 12, 11, 10, 9, 8, 7)
                        var hexagonCount = 0

                        // Calculate total height to center vertically
                        val totalHeight = (rowCounts.size - 1) * hexSizePx * 1.732f
                        val startY = centerY - totalHeight / 2

                        for ((rowIndex, colsInRow) in rowCounts.withIndex()) {
                            // Calculate total width to center horizontally for this row
                            val rowWidth = (colsInRow - 1) * hexSizePx * horizontalSpacingMultiplier + hexSizePx
                            val startX = centerX - rowWidth / 2

                            for (col in 0 until colsInRow) {
                                val x = startX + col * hexSizePx * horizontalSpacingMultiplier
                                val y = startY + rowIndex * hexSizePx * 1.732f

                                val hue = (hexagonCount * 2.83f) % 360f
                                val color = Color.hsv(hue = hue, saturation = 0.8f, value = 0.9f)
                                val isSelected = hexagonCount == selectedHexagonIndex

                                // Draw the hexagon
                                withTransform({
                                    translate(left = x, top = y)
                                }) {
                                    if (isSelected) {
                                        // Draw larger selected hexagon
                                        drawPath(
                                            path = selectedHexagonPath,
                                            color = color,
                                            style = Fill
                                        )

                                        drawPath(
                                            path = selectedHexagonPath,
                                            color = Color.Black, // Black border for selected
                                            style = Stroke(1.5.dp.toPx())
                                        )
                                    } else {
                                        // Draw normal hexagon
                                        drawPath(
                                            path = hexagonPath,
                                            color = color,
                                            style = Fill
                                        )

                                        drawPath(
                                            path = hexagonPath,
                                            color = Color.Gray, // Gray border for unselected
                                            style = Stroke(1.dp.toPx())
                                        )
                                    }
                                }

                                hexagonCount++
                            }
                        }
                    }

                    // Clickable overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(horizontalSpacingMultiplier) {
                                val hexSizePx = density.run { hexSize.toPx() }

                                detectTapGestures { offset ->
                                    val centerX = size.width / 2
                                    val centerY = size.height / 2

                                    val rowCounts = listOf(7, 8, 9, 10, 11, 12, 13, 12, 11, 10, 9, 8, 7)
                                    var hexagonCount = 0

                                    val totalHeight = (rowCounts.size - 1) * hexSizePx * 1.732f
                                    val startY = centerY - totalHeight / 2

                                    for ((rowIndex, colsInRow) in rowCounts.withIndex()) {
                                        val rowWidth = (colsInRow - 1) * hexSizePx * horizontalSpacingMultiplier + hexSizePx
                                        val startX = centerX - rowWidth / 2

                                        for (col in 0 until colsInRow) {
                                            val x = startX + col * hexSizePx * horizontalSpacingMultiplier
                                            val y = startY + rowIndex * hexSizePx * 1.732f

                                            // Check if tap is inside this hexagon
                                            val dx = offset.x - x
                                            val dy = offset.y - y
                                            val distance = sqrt(dx * dx + dy * dy)

                                            if (distance <= hexSizePx) {
                                                val hue = (hexagonCount * 2.83f) % 360f
                                                val color = Color.hsv(hue = hue, saturation = 0.8f, value = 0.9f)
                                                selectedHexagonIndex = hexagonCount
                                                onColorSelected(color)
                                                return@detectTapGestures
                                            }

                                            hexagonCount++
                                        }
                                    }
                                }
                            }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Radio buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = mode == "Select",
                            onClick = { onModeChanged("Select") }
                        )
                        Text("Select", modifier = Modifier.padding(start = 4.dp))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = mode == "ChangeToPreset",
                            onClick = { onModeChanged("ChangeToPreset") }
                        )
                        Text("Change to Preset", modifier = Modifier.padding(start = 4.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Preset boxes (only show when in ChangeToPreset mode)
                if (mode == "ChangeToPreset") {
                    val columns = 4
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(columns),
                        modifier = Modifier.height(120.dp)
                    ) {
                        itemsIndexed(customColors) { index, color ->
                            PresetBox(
                                color = color,
                                onRemove = { onPresetRemoved(index) },
                                isAddBox = false
                            )
                        }

                        // Add empty slots for new presets
                        items(8 - customColors.size) { index ->
                            PresetBox(
                                color = selectedColor,
                                onAdd = { onPresetAdded(customColors.size + index, selectedColor) },
                                isAddBox = true
                            )
                        }
                    }
                }
            }
        }
    )
}



@Composable
fun PresetBox(
    color: Color,
    onRemove: (() -> Unit)? = null,
    onAdd: (() -> Unit)? = null,
    isAddBox: Boolean = false
) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .border(
                width = 1.dp,
                color = if (isAddBox) Color.Gray.copy(alpha = 0.5f) else Color.Gray,
                shape = RoundedCornerShape(8.dp)
            )
            .size(60.dp, 40.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Color circle
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(color, CircleShape)
                    .border(1.dp, Color.Black, CircleShape)
            )

            // Action button (× or +)
            IconButton(
                onClick = {
                    if (isAddBox) onAdd?.invoke() else onRemove?.invoke()
                },
                modifier = Modifier.size(20.dp)
            ) {
                Icon(
                    imageVector = if (isAddBox) Icons.Default.Add else Icons.Default.Close,
                    contentDescription = if (isAddBox) "Add preset" else "Remove preset",
                    tint = Color.Black,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
