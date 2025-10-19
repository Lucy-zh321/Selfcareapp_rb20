package com.example.selfcare

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.LazyListState
import kotlin.math.abs
import androidx.compose.foundation.lazy.items  // For items() function
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx. compose. foundation. gestures. FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.runtime.mutableIntStateOf

// Add this right after your imports
val NoFlingBehavior = object : FlingBehavior {
    override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
        return 0f // This completely disables fling physics
    }
}

private fun calculateDuration(startHour: Int, startMinute: Int, endHour: Int, endMinute: Int): Int {
    val startTotal = startHour * 60 + startMinute
    val endTotal = endHour * 60 + endMinute
    return if (endTotal >= startTotal) {
        endTotal - startTotal
    } else {
        (24 * 60 - startTotal) + endTotal
    }
}

@Composable
fun TimeRangePickerDialog(
    selectedStartTime: String,
    selectedEndTime: String,
    onDismiss: () -> Unit,
    onTimeChanged: (String, String) -> Unit,
    onConfirm: (startTime: String, endTime: String) -> Unit
) {
    val startParts = selectedStartTime.split(":").mapNotNull { it.toIntOrNull() }
    val endParts = selectedEndTime.split(":").mapNotNull { it.toIntOrNull() }

    var startHour by remember { mutableIntStateOf(startParts.getOrElse(0) { 0 }) }
    var startMinute by remember { mutableIntStateOf(startParts.getOrElse(1) { 0 }) }
    var endHour by remember { mutableIntStateOf(endParts.getOrElse(0) { 0 }) }
    var endMinute by remember { mutableIntStateOf(endParts.getOrElse(1) { 0 }) }

    // Add this constant at the top of your file (with other constants/helpers)
    val NoFlingBehavior = object : FlingBehavior {
        override suspend fun ScrollScope.performFling(initialVelocity: Float): Float = 0f
    }
    // Track duration and auto-update state
    var durationMinutes by remember {
        mutableIntStateOf(calculateDuration(startHour, startMinute, endHour, endMinute))
    }
    var isAutoUpdatingEndTime by remember { mutableStateOf(false) }

    val hours = (0..23).toList()
    val minutes = (0..59).toList()

    val startHourState = rememberLazyListState(initialFirstVisibleItemIndex = startHour)
    val startMinuteState = rememberLazyListState(initialFirstVisibleItemIndex = startMinute)
    val endHourState = rememberLazyListState(initialFirstVisibleItemIndex = endHour)
    val endMinuteState = rememberLazyListState(initialFirstVisibleItemIndex = endMinute)

    val coroutineScope = rememberCoroutineScope()
    var isManualEndTimeAdjustment by remember { mutableStateOf(false) }
    var startTime by remember { mutableStateOf(selectedStartTime) }
    var endTime by remember { mutableStateOf(selectedEndTime) }


    val customFlingBehavior = remember {
        object : FlingBehavior {
            override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
                // Return 0 velocity to disable fling
                return 0f
            }
        }
    }

    // Auto-update end time when start time changes
    LaunchedEffect(startHour, startMinute) {
        // Only auto-update end time if we're not in manual mode
        if (!isManualEndTimeAdjustment) {
            isAutoUpdatingEndTime = true
            val newEndTotal = (startHour * 60 + startMinute + durationMinutes) % (24 * 60)
            val (clampedHour, clampedMinute) = if (newEndTotal < startHour * 60 + startMinute) {
                // This means we're wrapping past midnight
                23 to 59  // Force to 23:59
            } else {
                clampEndTime(newEndTotal / 60, newEndTotal % 60)
            }
            endHour = newEndTotal / 60
            endMinute = newEndTotal % 60
            coroutineScope.launch {
                endHourState.animateScrollToItem(endHour)
                endMinuteState.animateScrollToItem(endMinute)
            }
            val newStartTime = "${startHour.toString().padStart(2, '0')}:${startMinute.toString().padStart(2, '0')}"
            val newEndTime = "${endHour.toString().padStart(2, '0')}:${endMinute.toString().padStart(2, '0')}"
            onTimeChanged(newStartTime, newEndTime)
            isAutoUpdatingEndTime = false
        }
    }


    LaunchedEffect(endHour, endMinute) {
        if (!isAutoUpdatingEndTime) {
            val newEndTotal = endHour * 60 + endMinute
            val startTotal = startHour * 60 + startMinute

            if (newEndTotal < startTotal) {
                // Adjust start time backward to maintain duration
                val newStartTotal = (newEndTotal - durationMinutes).let {
                    if (it < 0) it + (24 * 60) else it
                }
                startHour = newStartTotal / 60
                startMinute = newStartTotal % 60

                coroutineScope.launch {
                    startHourState.animateScrollToItem(startHour)
                    startMinuteState.animateScrollToItem(startMinute)
                }
            } else {
                durationMinutes = newEndTotal - startTotal
            }
            val newStartTime = "${startHour.toString().padStart(2, '0')}:${startMinute.toString().padStart(2, '0')}"
            val newEndTime = "${endHour.toString().padStart(2, '0')}:${endMinute.toString().padStart(2, '0')}"
            onTimeChanged(newStartTime, newEndTime)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val startStr = "${startHour.toString().padStart(2, '0')}:${startMinute.toString().padStart(2, '0')}"
                val endStr = "${endHour.toString().padStart(2, '0')}:${endMinute.toString().padStart(2, '0')}"
                onConfirm(startStr, endStr)
            }) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Start Hour
                TimeWheel(
                    items = hours,
                    selected = startHour,
                    onSelect = { startHour = it },
                    format = { it.toString().padStart(2, '0') },
                    listState = startHourState,
                    modifier = Modifier.weight(1f),

                )

                Text(":")

                // Start Minute
                TimeWheel(
                    items = minutes,
                    selected = startMinute,
                    onSelect = { startMinute = it },
                    format = { it.toString().padStart(2, '0') },
                    listState = startMinuteState,
                    modifier = Modifier.weight(1f),

                )

                Text("—")

                // End Hour
                TimeWheel(
                    items = hours,
                    selected = endHour,
                    onSelect = { endHour = it },
                    format = { it.toString().padStart(2, '0') },
                    listState = endHourState,
                    modifier = Modifier.weight(1f),

                )

                Text(":")

                // End Minute
                TimeWheel(
                    items = minutes,
                    selected = endMinute,
                    onSelect = { endMinute = it },
                    format = { it.toString().padStart(2, '0') },
                    listState = endMinuteState,
                    modifier = Modifier.weight(1f),

                )
            }
        }
    )
}



@Composable
fun TimeWheel(
    items: List<Int>,
    selected: Int,
    onSelect: (Int) -> Unit,
    format: (Int) -> String,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(initialFirstVisibleItemIndex = items.indexOf(selected)),

) {
    val coroutineScope = rememberCoroutineScope()
    val itemHeight = 40.dp
    val visibleItemsCount = 5


    LazyColumn(
        state = listState,
        modifier = Modifier
            .height(itemHeight * visibleItemsCount)
            .width(60.dp),

        contentPadding = PaddingValues(vertical = itemHeight * (visibleItemsCount / 2))
    ) {
        items(items) { item ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(itemHeight),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = format(item),
                    color = if (item == selected) Color.Black else Color.Gray,
                    fontWeight = if (item == selected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }

    // Handle snapping manually
    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .distinctUntilChanged()
            .filter { !it } // When scrolling stops
            .collect {
                val layoutInfo = listState.layoutInfo
                val center = layoutInfo.viewportStartOffset + layoutInfo.viewportSize.height / 2
                val closest = layoutInfo.visibleItemsInfo.minByOrNull { item ->
                    abs(item.offset + item.size / 2 - center)
                }

                closest?.let {
                    coroutineScope.launch {
                        listState.animateScrollToItem(it.index)
                        onSelect(items[it.index])
                    }
                }
            }

    }
}









