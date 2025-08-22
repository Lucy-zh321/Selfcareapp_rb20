package com.example.selfcare

import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import kotlin.math.abs
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.zIndex
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import java.util.Locale

@Composable
fun TimePickerWheel(
    timeOptions: List<String>,
    selectedStartTime: String,
    selectedLength: String?,
    onTimeSelected: (String) -> Unit,
    onFinalTimeSelected: (String) -> Unit,
    calculateEndTime: (String, String) -> String,
    onLengthChanged: (String) -> Unit
) {
    // Configuration
    val visibleItemCount = 5
    val itemHeightDp = 35.dp
    val listState = rememberLazyListState()
    val haptic = LocalHapticFeedback.current
    val showDialog = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // State variables
    var manualTimeRange by remember { mutableStateOf<String?>(null) }
    var lastManualRange by remember { mutableStateOf<String?>(null) }
    val currentDurationMinutes = remember { mutableStateOf<Int?>(null) }
    var exactStartTime by remember { mutableStateOf<String?>(null) }
    var hasUserScrolled by remember { mutableStateOf(false) }

    // Display options
    val displayTimeOptions = remember(timeOptions) { timeOptions }


    var isCustomDuration by remember { mutableStateOf(false) }
    // Centered time tracking
    val centeredTime = remember {
        derivedStateOf {
            try {
                val layoutInfo = listState.layoutInfo
                val center = layoutInfo.viewportStartOffset + layoutInfo.viewportSize.height / 2
                layoutInfo.visibleItemsInfo.minByOrNull { item ->
                    abs(item.offset + item.size / 2 - center)
                }?.index?.let { idx ->
                    displayTimeOptions.getOrNull(idx.coerceIn(0, displayTimeOptions.lastIndex))
                } ?: selectedStartTime
            } catch (e: Exception) {
                selectedStartTime
            }
        }
    }

    // Display time calculation (UPDATED for "1" case)
    val displayTime = remember(
        centeredTime.value,
        selectedLength,
        manualTimeRange,
        currentDurationMinutes.value,
        exactStartTime,
        hasUserScrolled
    ) {
        try {
            when {
                selectedLength == "1" -> centeredTime.value // Just show the time for "1" selection
                exactStartTime != null && !hasUserScrolled -> {
                    if (currentDurationMinutes.value != null) {
                        "$exactStartTime - ${minutesToTime(timeToMinutes(exactStartTime!!) + currentDurationMinutes.value!!)}"
                    } else {
                        "$exactStartTime - ${calculateEndTime(exactStartTime!!, selectedLength ?: "1h")}"
                    }
                }
                manualTimeRange != null && currentDurationMinutes.value != null -> {
                    "${centeredTime.value} - ${minutesToTime(timeToMinutes(centeredTime.value) + currentDurationMinutes.value!!)}"
                }
                selectedLength != null -> calculateEndTime(centeredTime.value, selectedLength)
                else -> centeredTime.value
            }
        } catch (e: Exception) {
            centeredTime.value
        }
    }

    // Initial scroll
    LaunchedEffect(Unit) {
        listState.scrollToItem(
            displayTimeOptions.indexOf(selectedStartTime).coerceIn(0, displayTimeOptions.lastIndex)
        )
    }

    // Handle manual time range changes
    LaunchedEffect(manualTimeRange) {
        manualTimeRange?.let { range ->
            range.split(" - ").takeIf { it.size == 2 }?.let { (start, end) ->
                exactStartTime = start
                hasUserScrolled = false
                currentDurationMinutes.value = timeToMinutes(end) - timeToMinutes(start)

                coroutineScope.launch {
                    listState.scrollToItem(
                        timeOptions.indexOfFirst { timeToMinutes(it) >= timeToMinutes(start) - 7 }
                            .coerceIn(0, timeOptions.lastIndex)
                    )
                }
            }
        } ?: run {
            exactStartTime = null
            currentDurationMinutes.value = null
        }
    }

    // Handle duration changes (UPDATED for "1" case)
    LaunchedEffect(selectedLength) {
        selectedLength?.let { length ->
            if (length == "1") {
                // Reset to show only start time
                manualTimeRange = null
                lastManualRange = null
                currentDurationMinutes.value = null
                exactStartTime = centeredTime.value
                hasUserScrolled = false

                // Force update the display
                onTimeSelected(centeredTime.value)
                onFinalTimeSelected(centeredTime.value)
            }
            else if (manualTimeRange != null) {
                val parts = manualTimeRange!!.split(" - ")
                if (parts.size == 2) {
                    val newEndTime = calculateEndTime(parts[0], length).split(" - ").getOrNull(1) ?: return@let
                    manualTimeRange = "${parts[0]} - $newEndTime"
                    lastManualRange = manualTimeRange
                    currentDurationMinutes.value = timeToMinutes(newEndTime) - timeToMinutes(parts[0])
                }
            }
            else {
                // Handle standard duration changes
                val newEndTime = calculateEndTime(centeredTime.value, length).split(" - ").getOrNull(1) ?: return@let
                manualTimeRange = "${centeredTime.value} - $newEndTime"
                currentDurationMinutes.value = timeToMinutes(newEndTime) - timeToMinutes(centeredTime.value)
            }
        }
    }

    // Simple scroll handler (original behavior)
    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .collect { isScrolling ->
                if (isScrolling) {
                    hasUserScrolled = true
                } else if (hasUserScrolled) {
                    delay(50) // Small debounce

                    try {
                        val layoutInfo = listState.layoutInfo
                        val center = layoutInfo.viewportStartOffset + layoutInfo.viewportSize.height / 2
                        layoutInfo.visibleItemsInfo.minByOrNull { item ->
                            abs(item.offset + item.size / 2 - center)
                        }?.let { item ->
                            // Simple snapping without animation
                            listState.scrollToItem(item.index)

                            val selected = displayTimeOptions[item.index]
                            if (manualTimeRange == null) {
                                onTimeSelected(selected)
                                onFinalTimeSelected(selected)
                            } else if (currentDurationMinutes.value != null) {
                                onTimeSelected(selected)
                                manualTimeRange = "$selected - ${minutesToTime(timeToMinutes(selected) + currentDurationMinutes.value!!)}"
                                lastManualRange = manualTimeRange
                            }
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        }
                    } catch (e: Exception) {}
                }
            }
    }

    // Main UI
    Box(
        modifier = Modifier
            .height(itemHeightDp * visibleItemCount)
            .fillMaxWidth()
    ) {
        // Center display
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.7f)
                .height(itemHeightDp * 1.3f)
                .background(Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                .clickable { showDialog.value = true }
                .zIndex(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = displayTime,
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }

        // Wheel list with default scrolling
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = itemHeightDp * ((visibleItemCount - 1) / 2f)),
        ) {
            items(displayTimeOptions) { time ->
                Box(
                    modifier = Modifier
                        .height(itemHeightDp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = time,
                        fontSize = 17.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }

    // Dialog
    if (showDialog.value) {
        var dialogStartTime by remember {
            mutableStateOf(lastManualRange?.split(" - ")?.getOrNull(0) ?: centeredTime.value)
        }
        var dialogEndTime by remember {
            mutableStateOf(
                lastManualRange?.split(" - ")?.getOrNull(1) ?: displayTime.split(" - ").getOrNull(1)
                ?: calculateEndTime(centeredTime.value, selectedLength ?: "1h").split(" - ").getOrNull(1)
                ?: "00:00"
            )
        }

        TimeRangePickerDialog(
            selectedStartTime = dialogStartTime,
            selectedEndTime = dialogEndTime,
            onDismiss = { showDialog.value = false },
            onTimeChanged = { newStart, newEnd ->
                dialogStartTime = newStart
                dialogEndTime = newEnd
                manualTimeRange = "$newStart - $newEnd"
                currentDurationMinutes.value = timeToMinutes(newEnd) - timeToMinutes(newStart)
            },
            onConfirm = { start, end ->
                exactStartTime = start
                hasUserScrolled = false
                manualTimeRange = "$start - $end"
                lastManualRange = manualTimeRange
                currentDurationMinutes.value = timeToMinutes(end) - timeToMinutes(start)


                isCustomDuration = currentDurationMinutes.value !in listOf(15, 30, 45, 60, 90)
                onLengthChanged(
                    when (currentDurationMinutes.value) {
                        15 -> "15"
                        30 -> "30"
                        45 -> "45"
                        60 -> "1h"
                        90 -> "1.5h"
                        else -> {
                            isCustomDuration = true
                            "${currentDurationMinutes.value}m"}
                    }
                )
                onFinalTimeSelected(start)
                showDialog.value = false
            }
        )
    }
}




private fun formatTimeWithMinutes(time: String): String {
        return try {
            val parts = time.split(":")
            val hours = parts[0].toInt().coerceIn(0, 23)
            val minutes = parts.getOrNull(1)?.toIntOrNull()?.coerceIn(0, 59) ?: 0
            String.format(Locale.getDefault(), "%02d:%02d", hours, minutes)
        } catch (e: Exception) {
            "00:00"
        }
    }




// Helper functions






// NEW: Helper function to convert minutes back to time string
private fun minutesToTime(totalMinutes: Int): String {
    val hours = (totalMinutes / 60) % 24
    val minutes = totalMinutes % 60
    return String.format(Locale.getDefault(),"%02d:%02d", hours, minutes)
}




// Helper functions (unchanged)


private fun timeToMinutes(time: String): Int {
    return try {
        val parts = time.split(":")
        val hours = parts.getOrNull(0)?.toIntOrNull() ?: 0 // Safe access and conversion
        val minutes = parts.getOrNull(1)?.toIntOrNull() ?: 0 // Safe access and conversion
        hours * 60 + minutes
    } catch (e: Exception) {
        0 // Fallback value
    }
}


