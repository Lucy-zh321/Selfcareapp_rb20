package com.example.selfcare

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.selfcare.ui.theme.SelfCareTheme
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import kotlin.math.max

//private val String.hour: Any

data class Task(
    val id: Long,
    val name: String,
    val startTime: String,
    val endTime: String,
    val color: Color,
    val date: String,
    val repeatRule: RepeatRule? = null
)




// Add this to CalendarView.kt or create a new Models.kt file



// REPLACE your existing CalendarView composable with this:
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarView(
    tasks: List<Task> = emptyList(),
    onAddTask: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var weekOffset by remember { mutableIntStateOf(0) }
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.WEEK_OF_YEAR, weekOffset)

    val currentYear = calendar.get(Calendar.YEAR)
    val currentMonth = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
    val today = Calendar.getInstance()

    // Get Monday of this week
    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    val mondayOffset = if (dayOfWeek == Calendar.SUNDAY) -6 else Calendar.MONDAY - dayOfWeek
    calendar.add(Calendar.DATE, mondayOffset)

    val weekDates = (0..6).map {
        val date = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        calendar.add(Calendar.DATE, 1)
        Triple(date, month, year)
    }

    val timeSlots = (0..23).map { "$it:00" }
    val scrollState = rememberScrollState()
    val shortDayNames = listOf("M", "T", "W", "T", "F", "S", "S")

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val hourBlockHeight = 80.dp
        val timeLabelWidth = 45.dp

        val dayColumnWidth = remember(maxWidth) {
            val totalAvailableWidth = maxWidth - timeLabelWidth
            totalAvailableWidth / 7f
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { _, dragAmount ->
                        if (dragAmount > 50) weekOffset--
                        else if (dragAmount < -50) weekOffset++
                    }
                }
        ) {
            // FIXED HEADER SECTION - Doesn't scroll
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray.copy(alpha = 0.3f))
            ) {
                // Month and Year
                Text(
                    text = "$currentMonth $currentYear",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )

                // Days of Week Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = timeLabelWidth, bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    weekDates.forEachIndexed { index, (date, month, year) ->
                        val isToday = date == today.get(Calendar.DAY_OF_MONTH) &&
                                month == today.get(Calendar.MONTH) &&
                                year == today.get(Calendar.YEAR)

                        Box(
                            modifier = Modifier
                                .width(dayColumnWidth),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = shortDayNames[index],
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )

                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isToday) Color(0xFFF9C8D9) else Color.Transparent
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = date.toString(),
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Faint black line at the bottom of the header
            Divider(
                color = Color.Black.copy(alpha = 0.1f),
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth()
            )

            // SCROLLABLE CONTENT SECTION - Both white gap and grid scroll together
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    // WHITE GAP - Now part of scrollable content
                    Spacer(modifier = Modifier.height(16.dp))

                    // GRID SECTION - Scrolls with the white gap
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        // Time labels
                        Column(
                            modifier = Modifier.width(timeLabelWidth),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            timeSlots.forEach { timeLabel ->
                                Box(
                                    modifier = Modifier.height(hourBlockHeight),
                                    contentAlignment = Alignment.TopCenter
                                ) {
                                    Text(
                                        text = timeLabel,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }

                        // Grid
                        Box(
                            modifier = Modifier
                                .width(dayColumnWidth * 7f)
                                .clip(MaterialTheme.shapes.medium)
                                .background(Color(0xFFFFF0F4))
                                .border(1.dp, Color(0xFFB0B0B0), MaterialTheme.shapes.medium)
                        ) {
                            // Grid lines
                            Column {
                                repeat(timeSlots.size) { index ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(hourBlockHeight - 1.dp)
                                    ) {
                                        repeat(7) { columnIndex ->
                                            Box(
                                                modifier = Modifier
                                                    .width(dayColumnWidth)
                                                    .fillMaxHeight()
                                            ) {
                                                // Column content
                                            }
                                            if (columnIndex < 6) {
                                                VerticalDivider(
                                                    color = Color(0xFFCCCCCC),
                                                    thickness = 1.dp,
                                                    modifier = Modifier.fillMaxHeight()
                                                )
                                            }
                                        }
                                    }
                                    HorizontalDivider(
                                        color = Color(0xFFCCCCCC),
                                        thickness = 1.dp,
                                        modifier = Modifier.height(1.dp)
                                    )
                                }
                            }

                            // Tasks overlay
                            // In your CalendarView, find the task positioning section and replace it with this:

// Tasks overlay - FIXED: Precise positioning within grid cells
                            // In CalendarView task positioning:
                            Box(modifier = Modifier.matchParentSize()) {
                                // In CalendarView composable:
                                val currentWeekTasks = remember(weekDates, tasks, weekOffset) {
                                    tasks.flatMap { task ->
                                        getTaskOccurrencesForWeek(task, weekDates, weekOffset)
                                    }
                                }

                                currentWeekTasks.forEach { task ->
                                    val dayIndex = getDayIndexForTask(task, weekDates)
                                    if (dayIndex != -1) {
                                        // Position with the right offset (about 4 pixels = 3.dp)
                                        val xPosition = dayColumnWidth * dayIndex + 6.dp

                                        TaskBox(
                                            task = task,
                                            hourBlockHeight = hourBlockHeight,
                                            dayColumnWidth = dayColumnWidth,
                                            modifier = Modifier.offset(x = xPosition)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Floating Action Button - Fixed position in scrollable area
                FloatingActionButton(
                    onClick = onAddTask,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(20.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Task")
                }
            }
        }
    }
}
// Helper function to determine which day column a task belongs to


@Composable
fun TaskBox(
    task: Task,
    hourBlockHeight: Dp,
    dayColumnWidth: Dp,
    modifier: Modifier = Modifier
) {
    // Parse String times to get hours and minutes
    val startParts = task.startTime.split(":")
    val endParts = task.endTime.split(":")

    val startHour = startParts[0].toIntOrNull() ?: 0
    val startMinute = startParts.getOrNull(1)?.toIntOrNull() ?: 0
    val endHour = endParts[0].toIntOrNull() ?: 0
    val endMinute = endParts.getOrNull(1)?.toIntOrNull() ?: 0

    // Calculate total minutes from start of day
    val startTotalMinutes = startHour * 60 + startMinute
    val endTotalMinutes = endHour * 60 + endMinute

    // Calculate duration in minutes
    val durationMinutes = if (endTotalMinutes >= startTotalMinutes) {
        endTotalMinutes - startTotalMinutes
    } else {
        // Handle overnight tasks (end time next day)
        (24 * 60 - startTotalMinutes) + endTotalMinutes
    }

    // FIXED: Calculate proportional height and position correctly
    val minutesPerHourBlock = 60f
    val boxHeight = hourBlockHeight * (durationMinutes / minutesPerHourBlock)
    val topOffset = hourBlockHeight * (startTotalMinutes / minutesPerHourBlock)

    // Minimal font sizes
    val timeFontSize = 9.sp
    val taskFontSize = 10.sp
    val timeText = "${task.startTime}-${task.endTime}"

    Box(
        modifier = modifier
            .width(dayColumnWidth - 2.dp) // Fit between vertical grid lines
            .height(boxHeight - 2.dp) // Fit between horizontal grid lines
            .offset(y = topOffset + 1.dp) // Start after top grid line
            .background(
                task.color,
                RoundedCornerShape(4.dp)
            )
            .border(
                1.dp,
                Color.DarkGray,
                RoundedCornerShape(4.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 2.dp, vertical = 1.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Time at the top
            Text(
                text = timeText,
                color = Color.Black,
                fontSize = timeFontSize,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                softWrap = false
            )

            // Faint line under the time
            Divider(
                color = Color.Black.copy(alpha = 0.2f),
                thickness = 0.5.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 1.dp)
            )

            // Task name - use all remaining space
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = task.name,
                    color = Color.Black,
                    fontSize = taskFontSize,
                    fontWeight = FontWeight.Normal,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    lineHeight = 10.sp
                )
            }
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
fun createTaskFromInput(
    taskName: String,
    selectedTime: String,
    selectedDate: org.threeten.bp.LocalDate,
    selectedColor: Color?,
    repeatUnit: String,
    repeatDays: Set<Int>,
    repeatEndCondition: EndCondition,
    selectedLength: String? = null,
    manualTimeRange: String? = null
): Task {
    val startTime: String
    val endTime: String

    if (manualTimeRange != null) {
        val parts = manualTimeRange.split(" - ")
        startTime = parts.getOrNull(0)?.trim() ?: "09:00"
        endTime = parts.getOrNull(1)?.trim() ?: "10:00"
    } else {
        val timeRange = calculateEndTime(selectedTime, selectedLength ?: "1h")
        val parts = timeRange.split(" - ")

        if (parts.size == 2) {
            startTime = parts[0].trim()
            endTime = parts[1].trim()
        } else {
            startTime = timeRange
            endTime = calculateSimpleEndTime(startTime, selectedLength ?: "1h")
        }
    }

    // Convert ThreeTenBP LocalDate to java.time.LocalDate
    val taskLocalDate = try {
        LocalDate.of(selectedDate.year, selectedDate.monthValue, selectedDate.dayOfMonth)
    } catch (e: Exception) {
        LocalDate.now()
    }

    // ENHANCED: Handle all repetition types including custom
    val repeatRule = if (repeatUnit != "Once") {
        // Determine days of week - for weekly repetition
        val daysOfWeek = when {
            repeatUnit == "Week" && repeatDays.isNotEmpty() -> repeatDays
            repeatUnit == "Week" && repeatDays.isEmpty() -> {
                // Auto-set to the day of week of the selected date
                setOf(taskLocalDate.dayOfWeek.value - 1) // Convert to 0-based (Monday=0)
            }
            else -> null
        }

        // Determine end date
        val endDate = when (repeatEndCondition) {
            is EndCondition.OnDate -> {
                val endLocalDate = LocalDate.of(
                    repeatEndCondition.date.year,
                    repeatEndCondition.date.monthValue,
                    repeatEndCondition.date.dayOfMonth
                )
                endLocalDate.toString()
            }
            is EndCondition.AfterOccurrences -> {
                // Calculate end date based on occurrences (simplified - use 1 year as default)
                taskLocalDate.plusYears(1).toString()
            }
            else -> null // Never ends
        }

        RepeatRule(
            frequency = repeatUnit.lowercase(), // "day", "week", "month", "year"
            daysOfWeek = daysOfWeek,
            endDate = endDate
        )
    } else {
        null
    }

    return Task(
        id = System.currentTimeMillis(),
        name = taskName,
        startTime = startTime,
        endTime = endTime,
        color = selectedColor ?: Color(0xFF64B5F6),
        date = taskLocalDate.toString(),
        repeatRule = repeatRule
    )
}

// Add this helper function for single time calculations
private fun calculateSimpleEndTime(startTime: String, duration: String): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    val start = sdf.parse(startTime)
    val calendar = Calendar.getInstance().apply { time = start!! }

    val minutesToAdd = when (duration) {
        "1" -> 1
        "15" -> 15
        "30" -> 30
        "45" -> 45
        "1h" -> 60
        "1.5h" -> 90
        else -> 60 // Default to 1 hour
    }

    calendar.add(Calendar.MINUTE, minutesToAdd)
    return sdf.format(calendar.time)
}
// Add this at the very bottom of CalendarView.kt (after the CalendarView composable)


private fun isTaskInWeek(task: Task, weekDates: List<Triple<Int, Int, Int>>): Boolean {
    // Parse the task date string (format: "yyyy-MM-dd")
    val taskDateParts = task.date.split("-")
    if (taskDateParts.size != 3) return false

    val taskYear = taskDateParts[0].toIntOrNull() ?: return false
    val taskMonth = taskDateParts[1].toIntOrNull() ?: return false
    val taskDay = taskDateParts[2].toIntOrNull() ?: return false

    // Check if task date matches any date in the current week
    return weekDates.any { (day, month, year) ->
        // Note: Calendar months are 0-based (January=0), but our task dates are 1-based
        taskDay == day && taskMonth == month + 1 && taskYear == year
    }
}

// Add these functions to your CalendarView.kt file (outside the composable)



@RequiresApi(Build.VERSION_CODES.O)
private fun getDayIndexForTask(task: Task, weekDates: List<Triple<Int, Int, Int>>): Int {
    val taskDate = parseTaskDate(task.date) ?: return -1

    weekDates.forEachIndexed { index, (day, month, year) ->
        val weekDayDate = LocalDate.of(year, month + 1, day) // Convert to LocalDate
        if (taskDate == weekDayDate) {
            return index
        }
    }
    return -1
}

@RequiresApi(Build.VERSION_CODES.O)
private fun getTaskOccurrencesForWeek(
    task: Task,
    weekDates: List<Triple<Int, Int, Int>>,
    weekOffset: Int
): List<Task> {
    val occurrences = mutableListOf<Task>()

    // Parse the original task date
    val originalTaskDate = parseTaskDate(task.date) ?: return emptyList()

    // For each day in the current week, check if the task should appear
    weekDates.forEachIndexed { index, weekDate ->
        val weekDayDate = LocalDate.of(weekDate.third, weekDate.second + 1, weekDate.first)

        if (shouldTaskAppearOnDate(task, originalTaskDate, weekDayDate, weekOffset)) {
            // Create a copy of the task for this specific date
            occurrences.add(task.copy(date = weekDayDate.toString()))
        }
    }

    return occurrences
}

@RequiresApi(Build.VERSION_CODES.O)
private fun shouldTaskAppearOnDate(
    task: Task,
    originalTaskDate: LocalDate,
    targetDate: LocalDate,
    weekOffset: Int
): Boolean {
    // If it's a one-time task, check exact date match
    if (task.repeatRule == null) {
        return originalTaskDate == targetDate
    }

    // Don't show tasks before the original date
    if (targetDate.isBefore(originalTaskDate)) {
        return false
    }

    val repeatRule = task.repeatRule!!

    // Check end date condition
    if (repeatRule.endDate != null) {
        val endDate = LocalDate.parse(repeatRule.endDate)
        if (targetDate.isAfter(endDate)) {
            return false
        }
    }

    // Handle different repetition frequencies
    return when (repeatRule.frequency.lowercase()) {
        "day" -> isDailyRepetition(originalTaskDate, targetDate)
        "week" -> isWeeklyRepetition(originalTaskDate, targetDate, repeatRule.daysOfWeek)
        "month" -> isMonthlyRepetition(originalTaskDate, targetDate)
        "year" -> isYearlyRepetition(originalTaskDate, targetDate)
        else -> originalTaskDate == targetDate // Fallback to one-time
    }
}


@RequiresApi(Build.VERSION_CODES.O)
private fun parseTaskDate(dateString: String): LocalDate? {
    return try {
        // Try ISO format first (yyyy-MM-dd)
        LocalDate.parse(dateString)
    } catch (e: Exception) {
        try {
            // Try dd/MM/yyyy format
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            LocalDate.parse(dateString, formatter)
        } catch (e: Exception) {
            null
        }
    }
}

// Repetition helper functions




@RequiresApi(Build.VERSION_CODES.O)
private fun isDailyRepetition(originalDate: LocalDate, targetDate: LocalDate): Boolean {
    // Every day from original date onward
    return !targetDate.isBefore(originalDate)
}

@RequiresApi(Build.VERSION_CODES.O)
private fun isWeeklyRepetition(
    originalDate: LocalDate,
    targetDate: LocalDate,
    daysOfWeek: Set<Int>?
): Boolean {
    // If specific days are provided, check if target date matches any of them
    if (!daysOfWeek.isNullOrEmpty()) {
        // Convert Java DayOfWeek (Monday=1) to your app's day index (Monday=0)
        val targetDayIndex = (targetDate.dayOfWeek.value - 1) % 7
        return daysOfWeek.contains(targetDayIndex) && !targetDate.isBefore(originalDate)
    }

    // Otherwise, same day of week as original task, but only after original date
    return originalDate.dayOfWeek == targetDate.dayOfWeek && !targetDate.isBefore(originalDate)
}

@RequiresApi(Build.VERSION_CODES.O)
private fun isMonthlyRepetition(originalDate: LocalDate, targetDate: LocalDate): Boolean {
    // Same day of month, but only after original date
    return originalDate.dayOfMonth == targetDate.dayOfMonth && !targetDate.isBefore(originalDate)
}

@RequiresApi(Build.VERSION_CODES.O)
private fun isYearlyRepetition(originalDate: LocalDate, targetDate: LocalDate): Boolean {
    // Same month and day, but only after original date
    return originalDate.month == targetDate.month &&
            originalDate.dayOfMonth == targetDate.dayOfMonth &&
            !targetDate.isBefore(originalDate)
}