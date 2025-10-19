package com.example.selfcare

import android.R.attr.label
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
import androidx.compose.material3.TextField
import androidx.compose.foundation.background
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import org. threeten. bp. LocalDate
import org.threeten.bp.format.DateTimeFormatter
import android.app.Application
import android.util.Log
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import com.jakewharton.threetenabp.AndroidThreeTen
import androidx. compose. ui. platform. LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.filled.Check
import java.time.LocalTime


// Add these data classes at the TOP of AddTaskScreen.kt (after imports)


data class RepeatRule(
    val frequency: String,
    val daysOfWeek: Set<Int>? = null,
    val endDate: String? = null
)



data class SubtaskItem(
    val id: Int,
    var text: String,
    var isCompleted: Boolean = false
)


class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }
}
// Add this data class at the top level (outside of any function)

@Composable
fun AddTaskScreen(onBack: () -> Unit, onTaskAdded: (Task) -> Unit = {}) {
    // SAFETY: Initialize ThreeTenABP properly
    val context = LocalContext.current

    var isDateInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            AndroidThreeTen.init(context)
            isDateInitialized = true
        } catch (e: Exception) {
            isDateInitialized = true
            Log.e("Add Task", "LaunchedEffect error occurred: ${e.message}", e)
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
                Log.e("Add Task", "selectedDate error occurred: ${e.message}", e)
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
            Log.e("Add Task", "currentDate error occurred: ${e.message}", e)
            "Select Date"
        }
    }

    val formattedDate = remember(currentDate) {
        try {
            LocalDate.parse(currentDate).format(dateFormatter)
        } catch (e: Exception) {
            Log.e("Add Task", "formatted error occurred: ${e.message}", e)
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
    var selectedColor by remember { mutableStateOf<Color?>(null) }


    var showColorDialog by remember { mutableStateOf(false) }
    var customColors by remember {
        mutableStateOf(
            listOf(
                Color(0xFFFF8A80), // Coral Red
                Color(0xFFFFB74D), // Peach Orange
                Color(0xFFFFF176), // Soft Yellow
                Color(0xFFAED581), // Mint Green
                Color(0xFF64B5F6), // Sky Blue
                Color(0xFFBA68C8), // Lavender Purple
                Color(0xFFF48FB1), // Blush Pink
                Color(0xFF4DD0E1)  // Aqua Cyan
            )
        )
    }


    // Add these with your other state declarations in AddTaskScreen
    var dialogSelectedColor by remember { mutableStateOf(Color(0xFF64B5F6)) } // Color selected in the hexagon
    var dialogMode by remember { mutableStateOf("Select") } // "Select" or "ChangeToPreset"
    var dialogCustomColors by remember { mutableStateOf(customColors) } // Working copy of custom colors
    var tempPresets by remember { mutableStateOf(dialogCustomColors.toMutableList()) } // Temp list for editing
    var recentColors by remember { mutableStateOf<List<Color>>(emptyList()) }

    var notifications by remember { mutableStateOf<List<Pair<Int, String>>>(emptyList()) }
    var showAddNotificationDialog by remember { mutableStateOf(false) }
    var customNotificationTime by remember { mutableStateOf("") }
    var customNotificationUnit by remember { mutableStateOf("minutes") }
    var showNotificationOptionsDialog by remember { mutableStateOf(false) }
    var showCustomNotificationDialog by remember { mutableStateOf(false) }
    var mainNote by remember { mutableStateOf("") }
    var subtasks by remember { mutableStateOf<List<SubtaskItem>>(emptyList()) }



    // DELETE these lines (around line 140):

    // Add this data class outside of AddTaskScreen

    fun updateRecentColors(newColor: Color) {
        recentColors = listOf(newColor) + recentColors.filter { it != newColor }
        recentColors = recentColors.take(5) // Keep only the 5 most recent
    }

    // UI Layout - Using Box as root container
    Box(modifier = Modifier.fillMaxSize()) {
        val scrollState = rememberScrollState()


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Task Name Input Card
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp),
                colors = CardDefaults.cardColors(
                    containerColor = getSelectedColor(selectedColor, Color.White) // Changed background color
                )
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
                        manualTimeRange =
                            if (isCustomDuration(selectedLength) && isCustomDuration(newLength)) {
                                manualTimeRange
                            } else {
                                null
                            }
                        selectedLength = newLength
                    },
                    selectedColor = selectedColor
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
                options = lengthOptions,
                selectedColor = selectedColor, // Add this parameter
                isCustomDuration = isCustomDuration
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
                                        "Once" -> if (repeatUnit == "Once") getSelectedColor(selectedColor).copy(alpha = 0.3f) else Color.Transparent
                                        "Daily" -> if (repeatUnit == "Day" && repeatInterval == 1 && repeatDays.isEmpty()) getSelectedColor(selectedColor).copy(alpha = 0.3f) else Color.Transparent
                                        "Weekly" -> if (repeatUnit == "Week" && repeatInterval == 1 && repeatDays.isEmpty()) getSelectedColor(selectedColor).copy(alpha = 0.3f) else Color.Transparent
                                        "Monthly" -> if (repeatUnit == "Month" && repeatInterval == 1 && repeatDays.isEmpty()) getSelectedColor(selectedColor).copy(alpha = 0.3f) else Color.Transparent
                                        "Custom" -> if (repeatUnit != "Once" &&
                                            !(repeatUnit == "Day" && repeatInterval == 1 && repeatDays.isEmpty()) &&
                                            !(repeatUnit == "Week" && repeatInterval == 1 && repeatDays.isEmpty()) &&
                                            !(repeatUnit == "Month" && repeatInterval == 1 && repeatDays.isEmpty())
                                        ) getSelectedColor(selectedColor).copy(alpha = 0.3f) else Color.Transparent
                                        else -> Color.Transparent
                                    }
                                )
                                .clickable {
                                    when (option) {
                                        "Once" -> {
                                            repeatInterval = 1
                                            repeatUnit = "Once"
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
            Spacer(modifier = Modifier.height(16.dp))

// Color Selector with three dots on the right
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Color",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .weight(1f)
                )

                // Three dots on the right side (above the box)
                IconButton(
                    onClick = { showColorDialog = true },
                    modifier = Modifier.size(24.dp)
                ) {
                    Text("⋯", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp) // Slightly smaller height
                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val colors = listOf(
                        Color(0xFFFF8A80), // Coral Red
                        Color(0xFFFFB74D), // Peach Orange
                        Color(0xFFFFF176), // Soft Yellow
                        Color(0xFFAED581), // Mint Green
                        Color(0xFF64B5F6), // Sky Blue
                        Color(0xFFBA68C8), // Lavender Purple
                        Color(0xFFF48FB1), // Blush Pink
                        Color(0xFF4DD0E1)
                    )

                    // Display the 8 preset colors + recent colors
                    val allColorsToShow = remember(recentColors, customColors, selectedColor) {
                        val recent = recentColors.filter { it !in customColors }
                        (recent + customColors).distinct()
                            .take(8) // Ensure we only show 8 colors max
                    }

                    allColorsToShow.forEach { color ->
                        val isSelected = color == selectedColor
                        ColorCircle(
                            color = color,
                            isSelected = isSelected,
                            onSelected = {
                                val newSelection = if (selectedColor == color) null else color
                                selectedColor = newSelection
                                newSelection?.let { selectedColorValue ->
                                    updateRecentColors(selectedColorValue) // This will now work!
                                    dialogSelectedColor =
                                        selectedColorValue // Update dialog selection too
                                }
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }
            // Notifications Section
            // Notifications Section
            // Notifications Section
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Notifications",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                // Display each notification in its own box with friendly text
                notifications.forEachIndexed { index, (time, unit) ->
                    val displayText = when {
                        unit == "at_start" -> "At start of task"
                        time == 1 && unit == "hours" -> "1 hour before"
                        time == 1 && unit == "minutes" -> "1 minute before"
                        time == 1 && unit == "days" -> "1 day before"
                        time == 1 && unit == "weeks" -> "1 week before"
                        else -> "$time ${unit} before"
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Notification",
                                modifier = Modifier.size(20.dp),
                                tint = Color.DarkGray // Or Color.Gray for a neutral look
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = displayText,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove notification",
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable {
                                        notifications = notifications.toMutableList().apply { removeAt(index) }
                                    }
                            )
                        }
                    }

                    // Add spacing after each notification box
                    if (index < notifications.size - 1) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                // Add space before the add button if there are existing notifications
                if (notifications.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Add notification button in its own box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                        .clickable { showNotificationOptionsDialog = true }
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add notification",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Add notification",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))


// Details Section
            Text(
                text = "Details",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.Gray, RoundedCornerShape(12.dp)) // Curved corners for the whole box
            ) {
                Column {
                    // Pink Header Bar with + Add Subtask
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .background(getSelectedColor(selectedColor), RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)) // Changed color
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable {
                                    // Generate unique ID for new subtask
                                    val newId = subtasks.maxOfOrNull { it.id }?.plus(1) ?: 0
                                    subtasks = subtasks + SubtaskItem(id = newId, text = "")
                                }
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add subtask",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Add Subtask",
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Subtasks List
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        subtasks.forEachIndexed { index, subtask ->
                            SubtaskRow(
                                subtask = subtask,
                                selectedColor = selectedColor, // Pass the selected color
                                onTextChanged = { newText ->
                                    subtasks = subtasks.mapIndexed { i, item ->
                                        if (i == index) item.copy(text = newText) else item
                                    }
                                },
                                onCompletedChanged = { completed ->
                                    subtasks = subtasks.mapIndexed { i, item ->
                                        if (i == index) item.copy(isCompleted = completed) else item
                                    }
                                },
                                onDelete = {
                                    subtasks = subtasks.filterIndexed { i, _ -> i != index }
                                }
                            )
                        }

                        // Main Notes Text Field
                        // Main Notes Text Field - made smaller
                        BasicTextField(
                            value = mainNote,
                            onValueChange = { mainNote = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                                .height(120.dp), // Set a fixed height for multi-line
                            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                            decorationBox = { innerTextField ->
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.TopStart // This makes text start from top
                                ) {
                                    if (mainNote.isEmpty()) {
                                        Text(
                                            "Add detailed notes about your task...",
                                            fontSize = 14.sp,
                                            color = Color.Gray
                                        )
                                    }
                                    innerTextField()
                                }
                            },
                            maxLines = 4
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Add this composable function outside of AddTaskScreen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Save Task",
                    modifier = Modifier
                        .size(60.dp)
                        .background(getSelectedColor(selectedColor), CircleShape)
                        .clickable {
                            val newTask = createTaskFromInput(
                                taskName = taskName,
                                selectedTime = selectedTime,
                                selectedDate = selectedDate,
                                selectedColor = selectedColor,
                                repeatUnit = repeatUnit,
                                repeatDays = repeatDays,
                                repeatEndCondition = repeatEndCondition
                            )
                            onTaskAdded(newTask)
                            onBack()
                        }
                        .padding(12.dp),
                    tint = Color.White
                )
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
                },
                selectedColor = selectedColor
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
        if (showColorDialog) {
            ColorCustomizationDialog(
                selectedColor = dialogSelectedColor,
                onColorSelected = { newColor -> dialogSelectedColor = newColor },
                mode = dialogMode,
                onModeChanged = { newMode -> dialogMode = newMode },
                customColors = tempPresets,
                onPresetRemoved = { index ->
                    tempPresets = tempPresets.toMutableList().apply { removeAt(index) }
                },
                onPresetAdded = { index, color ->
                    tempPresets = tempPresets.toMutableList().apply { add(index, color) }
                },
                onConfirm = {
                    dialogCustomColors = tempPresets
                    customColors = tempPresets
                    showColorDialog = false
                },
                onDismiss = {
                    showColorDialog = false
                    tempPresets = dialogCustomColors.toMutableList() // Reset on cancel
                }
            )
        }
        if (showCustomNotificationDialog) {
            AlertDialog(
                onDismissRequest = { showCustomNotificationDialog = false },
                title = { Text("Custom Alert Time") },
                text = {
                    Column {
                        // Text input
                        OutlinedTextField(
                            value = customNotificationTime,
                            onValueChange = { customNotificationTime = it },
                            label = { Text("Time amount") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Radio buttons for units
                        Column {
                            listOf("minutes", "hours", "days", "weeks").forEach { unit ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { customNotificationUnit = unit }
                                        .padding(vertical = 4.dp)
                                ) {
                                    RadioButton(
                                        selected = customNotificationUnit == unit,
                                        onClick = { customNotificationUnit = unit }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("$unit before")
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val time = customNotificationTime.toIntOrNull() ?: 0
                            if (time > 0) {
                                notifications = notifications + Pair(time, customNotificationUnit)
                                customNotificationTime = ""
                                showCustomNotificationDialog = false // Auto-close on add
                            }
                        }
                    ) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showCustomNotificationDialog = false // Just close, no action
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
    if (showNotificationOptionsDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationOptionsDialog = false },
            title = { Text("Add Notification") },
            text = {
                Column {
                    val quickOptions = listOf(
                        Pair("At start of task", Pair(0, "at_start")),
                        Pair("5 minutes before", Pair(5, "minutes")),
                        Pair("15 minutes before", Pair(15, "minutes")),
                        Pair("30 minutes before", Pair(30, "minutes")),
                        Pair("1 hour before", Pair(1, "hours")),
                        Pair("Custom...", Pair(0, "custom"))
                    )

                    quickOptions.forEach { (label, timeUnitPair) ->
                        val (time, unit) = timeUnitPair
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (unit == "custom") {
                                        showNotificationOptionsDialog = false
                                        showCustomNotificationDialog = true
                                    } else {
                                        notifications = notifications + Pair(time, unit)
                                        showNotificationOptionsDialog = false
                                    }
                                }
                                .padding(vertical = 12.dp)
                        ) {
                            RadioButton(
                                selected = false,
                                onClick = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = label)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showNotificationOptionsDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                ) {
                    Text("Cancel")
                }
            }
        )
    }
    // Add this at the bottom of your Column (before the last closing brace)
    Spacer(modifier = Modifier.height(16.dp))

// Save Button

}



/**
 * Adds a color to the recent list, moving it to the front.
 * Removes duplicates and keeps the list to a manageable size.
 */

// Helper functions
private fun timeToMinutes(time: String): Int {
    return try {
        val parts = time.split(":")
        val hours = parts.getOrNull(0)?.toIntOrNull() ?: 0 // Safe access and conversion
        val minutes = parts.getOrNull(1)?.toIntOrNull() ?: 0 // Safe access and conversion
        hours * 60 + minutes
    } catch (e: Exception) {
        Log.e("Add Task", "timeToMinutes error occurred: ${e.message}", e)
        0 // Fallback value
    }
}

/*private fun isStandardDuration(minutes: Int): Boolean {
    return minutes in listOf(1, 15, 30, 45, 60, 90) // Matches your standard options
}

private fun calculateDuration(startTime: String, endTime: String): Int {
    return timeToMinutes(endTime) - timeToMinutes(startTime) // Uses your existing function
}*/

@Composable
fun SubtaskRow(
    subtask: SubtaskItem,
    selectedColor: Color?, // Add this parameter
    onTextChanged: (String) -> Unit,
    onCompletedChanged: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = subtask.isCompleted,
            onCheckedChange = onCompletedChanged,
            colors = CheckboxDefaults.colors(
                checkedColor = getSelectedColor(selectedColor) // Use the utility function
            ),
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(4.dp))

        BasicTextField(
            value = subtask.text,
            onValueChange = onTextChanged,
            modifier = Modifier
                .weight(1f)
                .height(36.dp)
                .padding(vertical = 8.dp),
            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
            singleLine = true,
            decorationBox = { innerTextField ->
                Column {
                    innerTextField()
                    Divider(
                        color = if (subtask.text.isNotEmpty()) getSelectedColor(selectedColor) else Color.Gray,
                        thickness = 1.dp
                    )
                }
            }
        )

        Spacer(modifier = Modifier.width(4.dp))

        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Delete subtask",
            modifier = Modifier
                .size(18.dp)
                .clickable { onDelete() },
            tint = Color.Gray
        )
    }
}

@Composable
fun LengthSelector(
    selectedOption: String?,
    onLengthSelected: (String) -> Unit,
    options: List<String>,
    selectedColor: Color?,
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
                                    isCustomDuration && selectedOption == label -> getSelectedColor(selectedColor).copy(alpha = 0.8f)
                                    selectedOption == label -> getSelectedColor(selectedColor).copy(alpha = 0.3f)
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

// Add this at the bottom of AddTaskScreen.kt (outside the composable)
// Replace the createTaskFromInput function at the bottom with this:



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
// Add this function at the top level (outside any composable)
@Composable
fun getSelectedColor(selectedColor: Color?, defaultColor: Color = Color(0xFF64B5F6)): Color {
    return selectedColor ?: defaultColor
}

// For alpha variants
@Composable
fun getSelectedColorWithAlpha(selectedColor: Color?, alpha: Float = 0.3f): Color {
    return getSelectedColor(selectedColor).copy(alpha = alpha)
}