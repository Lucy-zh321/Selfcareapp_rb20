package com.example.selfcare

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.TextField
import androidx.compose.foundation.background
import androidx.compose.ui.text.font.FontWeight
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import com.example.selfcare.timeToMinutes
import android. R. attr. onClick
import org. threeten. bp. LocalDate
import org.threeten.bp.format.DateTimeFormatter
import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.filled.Check
import com.jakewharton.threetenabp.AndroidThreeTen
import androidx. compose. ui. platform. LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.Locale



class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }
}

@Composable
fun AddTaskScreen(onBack: () -> Unit) {
    // SAFETY: Initialize ThreeTenABP properly
    val context = LocalContext.current
    var isDateInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            AndroidThreeTen.init(context)
            isDateInitialized = true
        } catch (e: Exception) {
            isDateInitialized = true
        }
    }

    // State declarations
    var taskName by remember { mutableStateOf("") }
    var showTimeDialog by remember { mutableStateOf(false) }
    var selectedTime by remember { mutableStateOf("09:00") }
    var selectedLength by remember { mutableStateOf<String?>("1h") }
    var manualTimeRange by remember { mutableStateOf<String?>(null) }
    var isCustomDuration by remember { mutableStateOf(false) }
    var autoSelectedDuration by remember { mutableStateOf<String?>(null) }

    // SAFETY: Null-safe time calculations
    val displayTime = remember(selectedTime, selectedLength, manualTimeRange) {
        manualTimeRange ?: selectedLength?.let { length ->
            calculateEndTime(selectedTime, length)
        } ?: selectedTime
    }
    val dateFormatter = remember {
        DateTimeFormatter.ofPattern("dd/MM/yyyy")
    }
    val durationMinutes = remember(manualTimeRange, selectedLength) {
        manualTimeRange?.let { calculateDurationFromRange(it) }
            ?: selectedLength?.let { parseDurationToMinutes(it) }
    }

    val lengthOptions = remember(durationMinutes) {
        val base = listOf("1", "15", "30", "45", "1h")
        if (durationMinutes != null && durationMinutes !in listOf(1, 15, 30, 45, 60, 90)) {
            base + formatMinutesToDuration(durationMinutes)
        } else {
            base + "1.5h"
        }
    }

    val timeOptions = remember { generateTimeOptions() }

    // SAFETY: Date initialization with fallback
    var selectedDate by remember {
        mutableStateOf(
            try {
                if (isDateInitialized) LocalDate.now() else LocalDate.of(2025, 8, 10)
            } catch (e: Exception) {
                LocalDate.of(2025, 8, 10) // Fallback date (October 8, 2025)
            }
        )
    }

    val currentDate = remember(selectedDate) {
        try {
            val month = when (selectedDate.monthValue) {
                1 -> "01"
            2 -> "02"
                3 -> "03"
                4 -> "04"
                5 -> "05"
                6 -> "06"
                7 -> "07"
                8 -> "08"
                9 -> "09"
                10 -> "10"
                11 -> "11"
                12 -> "12"
                else -> ""
            }
            "${selectedDate.dayOfMonth}/${month}/${selectedDate.year}"
        } catch (e: Exception) {
            "Select Date"
        }
    }

    val formattedDate = remember(currentDate) {
        try {
            LocalDate.parse(currentDate).format(dateFormatter)
        } catch (e: Exception) {
            currentDate // fallback to original if parsing fails
        }
    }
    var showDatePicker by remember { mutableStateOf(false) }




    // Add these with your other state declarations
    var showCustomRepeatDialog by remember { mutableStateOf(false) }
    var repeatInterval by remember { mutableStateOf(1) }
    var repeatUnit by remember { mutableStateOf("Week") }
    var repeatDays by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var repeatEndCondition by remember { mutableStateOf<EndCondition>(EndCondition.Never) }




    // UI Layout - Using Box as root container
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Task Name Input Card
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    TextField(
                        value = taskName,
                        onValueChange = { taskName = it },
                        placeholder = { Text("Add task...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(13.dp))

            Text(
                text = "Time",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Time Picker Wheel
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        color = MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            ) {
                TimePickerWheel(
                    timeOptions = timeOptions,
                    selectedStartTime = selectedTime,
                    selectedLength = selectedLength,
                    onTimeSelected = { newTime ->
                        selectedTime = newTime
                        manualTimeRange = null
                    },
                    onFinalTimeSelected = { newTime ->
                        selectedTime = newTime
                        manualTimeRange = null
                    },
                    calculateEndTime = ::calculateEndTime,
                    onLengthChanged = { newLength ->
                        manualTimeRange = if (isCustomDuration(selectedLength) && isCustomDuration(newLength)) {
                            manualTimeRange
                        } else {
                            null
                        }
                        selectedLength = newLength
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Date Display
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.CalendarToday,
                        contentDescription = "Calendar",
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = formattedDate,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Duration Selector
            LengthSelector(
                selectedOption = selectedLength.takeIf { manualTimeRange == null }
                    ?: durationMinutes?.let { formatMinutesToDuration(it) },
                onLengthSelected = { newLength ->
                    selectedLength = newLength
                    manualTimeRange = null
                },
                options = lengthOptions
            )
            Spacer(modifier = Modifier.height(16.dp))


            //repetition selector


            Text(
                text = "Repetition",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // In your Repetition selector (AddTaskScreen.kt)
            // In your Repetition selector (AddTaskScreen.kt)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf("Once", "Daily", "Weekly", "Monthly", "Custom").forEach { option ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp)
                                .height(42.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    when (option) {
                                        "Once" -> if (!showCustomRepeatDialog && repeatUnit == "Once") Color(0xFFFFE0E0) else Color.Transparent
                                        "Daily" -> if (!showCustomRepeatDialog && repeatUnit == "Day" && repeatInterval == 1) Color(0xFFFFE0E0) else Color.Transparent
                                        "Weekly" -> if (!showCustomRepeatDialog && repeatUnit == "Week" && repeatInterval == 1) Color(0xFFFFE0E0) else Color.Transparent
                                        "Monthly" -> if (!showCustomRepeatDialog && repeatUnit == "Month" && repeatInterval == 1) Color(0xFFFFE0E0) else Color.Transparent
                                        "Custom" -> if (showCustomRepeatDialog || (repeatUnit != "Once" && !(repeatInterval == 1 && repeatUnit in listOf("Day", "Week", "Month")))) Color(0xFFFFE0E0) else Color.Transparent
                                        else -> Color.Transparent
                                    }
                                )
                                .clickable {
                                    when (option) {
                                        "Once" -> {
                                            repeatInterval = 1
                                            repeatUnit = "Once" // Special case for one-time task
                                            repeatDays = emptySet()
                                            repeatEndCondition = EndCondition.Never
                                            showCustomRepeatDialog = false
                                        }
                                        "Daily" -> {
                                            repeatInterval = 1
                                            repeatUnit = "Day"
                                            repeatDays = emptySet()
                                            repeatEndCondition = EndCondition.Never
                                            showCustomRepeatDialog = false
                                        }
                                        "Weekly" -> {
                                            repeatInterval = 1
                                            repeatUnit = "Week"
                                            repeatDays = emptySet()
                                            repeatEndCondition = EndCondition.Never
                                            showCustomRepeatDialog = false
                                        }
                                        "Monthly" -> {
                                            repeatInterval = 1
                                            repeatUnit = "Month"
                                            repeatDays = emptySet()
                                            repeatEndCondition = EndCondition.Never
                                            showCustomRepeatDialog = false
                                        }
                                        "Custom" -> showCustomRepeatDialog = true
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        if (showCustomRepeatDialog) {
            CustomRepeatDialog(
                initialInterval = repeatInterval,
                initialUnit = repeatUnit,
                initialDays = repeatDays,
                initialEndCondition = repeatEndCondition,
                onDismiss = { showCustomRepeatDialog = false },
                onConfirm = { interval, unit, days, endCondition ->
                    repeatInterval = interval
                    repeatUnit = unit
                    repeatDays = days
                    repeatEndCondition = endCondition
                    showCustomRepeatDialog = false
                }
            )
        }

        // DIALOGS AT ROOT LEVEL
        if (showTimeDialog) {
            TimeRangePickerDialog(
                selectedStartTime = selectedTime,
                selectedEndTime = displayTime.split(" - ").getOrNull(1) ?: "10:00",
                onDismiss = { showTimeDialog = false },
                onTimeChanged = { newStart, newEnd ->
                    selectedTime = newStart
                    manualTimeRange = "$newStart - $newEnd"
                    val durationMinutes = timeToMinutes(newEnd) - timeToMinutes(newStart)
                    selectedLength = when (durationMinutes) {
                        1 -> "1"
                        15 -> "15"
                        30 -> "30"
                        45 -> "45"
                        60 -> "1h"
                        90 -> "1.5h"
                        else -> "${durationMinutes}m"
                    }
                },
                onConfirm = { newStart, newEnd ->
                    selectedTime = newStart
                    manualTimeRange = "$newStart - $newEnd"
                    showTimeDialog = false
                    val minutes = timeToMinutes(newEnd) - timeToMinutes(newStart)
                    val formattedDuration = formatMinutesToDuration(minutes)
                    isCustomDuration = minutes !in listOf(1, 15, 30, 45, 60, 90)
                    if (isCustomDuration) {
                        autoSelectedDuration = formattedDuration
                        selectedLength = formattedDuration
                    } else {
                        autoSelectedDuration = null
                        selectedLength = when (minutes) {
                            1 -> "1"
                            15 -> "15"
                            30 -> "30"
                            45 -> "45"
                            60 -> "1h"
                            90 -> "1.5h"
                            else -> selectedLength
                        }
                    }
                }
            )
        }

        if (showDatePicker) {
            DatePickerDialog(
                initialDate = selectedDate,
                onDateSelected = { newDate ->
                    selectedDate = newDate
                    showDatePicker = false
                },
                onDismiss = { showDatePicker = false }
            )
        }
    }
}




// Helper functions
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
private fun isStandardDuration(minutes: Int): Boolean {
    return minutes in listOf(1, 15, 30, 45, 60, 90) // Matches your standard options
}

private fun calculateDuration(startTime: String, endTime: String): Int {
    return timeToMinutes(endTime) - timeToMinutes(startTime) // Uses your existing function
}








@Composable
fun LengthSelector(
    selectedOption: String?,
    onLengthSelected: (String) -> Unit,
    options: List<String>,
    isCustomDuration: Boolean = false // NEW: Track if duration is custom
) {
    Column {
        Text(
            text = "Length",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                options.forEach { label ->
                    Box(
                        modifier = Modifier
                            .size(width = 60.dp, height = 42.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                when {
                                    // Bright pink for custom duration matches
                                    isCustomDuration && selectedOption == label -> Color(0xFFFFC0CB)
                                    // Light pink for standard selection
                                    selectedOption == label -> Color(0xFFFFE0E0)
                                    // Transparent for unselected
                                    else -> Color.Transparent
                                }
                            )
                            .clickable { onLengthSelected(label) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

private fun formatMinutesToDuration(minutes: Int): String {
    return when {
        minutes < 60 -> "${minutes}m"
        minutes % 60 == 0 -> "${minutes/60}h"
        else -> "${minutes/60}h${minutes%60}m"
    }
}

private fun parseDurationToMinutes(duration: String): Int? {
    return when {
        duration.endsWith("h") -> duration.removeSuffix("h").toIntOrNull()?.times(60)
        duration.endsWith("m") -> duration.removeSuffix("m").toIntOrNull()
        else -> duration.toIntOrNull()
    }
}
