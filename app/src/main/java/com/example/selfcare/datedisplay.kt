package com.example.selfcare
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.threeten.bp.LocalDate
import org.threeten.bp.Month
import org.threeten.bp.format.TextStyle
import androidx. compose. foundation. background
import java.util.Locale
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.draw.clip
import androidx. compose. ui. input. pointer. pointerInput
import androidx. compose. foundation. gestures. detectHorizontalDragGestures
import kotlin. math. abs
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import org.threeten.bp.YearMonth
import java.util.concurrent.ThreadLocalRandom
import java. util. concurrent. ThreadLocalRandom. current
import androidx. compose. foundation. pager. rememberPagerState




import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager

import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    initialDate: org.threeten.bp.LocalDate = org.threeten.bp.LocalDate.now(),
    onDateSelected: (org.threeten.bp.LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    // State for the current month being displayed
    val initialMonth = YearMonth.of(initialDate.year, initialDate.monthValue)
    var currentMonth by remember { mutableStateOf(initialMonth) }

    // State for the selected day
    var selectedDay by remember { mutableStateOf(initialDate.dayOfMonth) }

    // Pager state for smooth scrolling
    val pagerState = rememberPagerState(
        initialPage = Int.MAX_VALUE / 2,
        pageCount = { Int.MAX_VALUE }
    )
    val coroutineScope = rememberCoroutineScope()

    // Calculate current month based on pager position
    val displayedMonth by remember(pagerState.currentPage) {
        derivedStateOf {
            initialMonth.plusMonths((pagerState.currentPage - Int.MAX_VALUE / 2).toLong())
        }
    }

    // Sync currentMonth with displayedMonth
    LaunchedEffect(displayedMonth) {
        currentMonth = displayedMonth
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.width(400.dp),
        title = {},
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Year controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 12)
                            }
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Previous Year"
                        )
                    }

                    Text(
                        text = currentMonth.year.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 12)
                            }
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Next Year"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Month controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Previous Month"
                        )
                    }

                    Text(
                        text = currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault()),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Next Month"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Day headers - single letters
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("S", "M", "T", "W", "T", "F", "S").forEach { day ->
                        Text(
                            text = day,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(40.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // SMOOTH SCROLLING PAGER
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    pageSpacing = 16.dp
                ) { page ->
                    // Calculate month for this page
                    val month = initialMonth.plusMonths((page - Int.MAX_VALUE / 2).toLong())

                    CalendarGrid(
                        currentMonth = month,
                        selectedDay = selectedDay,
                        onDaySelected = { day -> selectedDay = day }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    try {
                        val selectedDate = LocalDate.of(
                            currentMonth.year,
                            currentMonth.monthValue,
                            selectedDay
                        )
                        onDateSelected(selectedDate)
                    } catch (e: Exception) {
                        onDismiss()
                    }
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun CalendarGrid(
    currentMonth: YearMonth,
    selectedDay: Int,
    onDaySelected: (Int) -> Unit
) {
    // Calculate days for the current month
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfWeek = LocalDate.of(currentMonth.year, currentMonth.monthValue, 1)
        .dayOfWeek.value % 7 // Sunday=0

    // Generate calendar cells (null for empty cells)
    val calendarCells = remember(currentMonth) {
        val cells = mutableListOf<Int?>()
        // Add empty cells for days before the first day
        repeat(firstDayOfWeek) { cells.add(null) }
        // Add actual days
        for (day in 1..daysInMonth) {
            cells.add(day)
        }
        // Fill remaining cells to make 42 cells total (6 rows)
        while (cells.size < 42) {
            cells.add(null)
        }
        cells
    }

    val today = LocalDate.now()
    val isToday = { day: Int? ->
        day != null && LocalDate.of(currentMonth.year, currentMonth.monthValue, day) == today
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
    ) {
        // Create 6 rows
        for (row in 0 until 6) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Create 7 columns per row
                for (col in 0 until 7) {
                    val index = row * 7 + col
                    val day = calendarCells.getOrNull(index)
                    val isTodayDate = isToday(day)
                    val isSelected = day == selectedDay

                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(40.dp)
                            .padding(vertical = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (day != null) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .border(
                                        width = if (isTodayDate && !isSelected) 1.5.dp else 0.dp,
                                        color = if (isTodayDate) Color(0xFFFFB6C1) else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .background(
                                        color = if (isSelected) Color(0xFFFFB6C1) else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { onDaySelected(day) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day.toString(),
                                    fontSize = 16.sp,
                                    color = when {
                                        isSelected -> Color.White
                                        isTodayDate -> Color(0xFFFFB6C1)
                                        else -> MaterialTheme.colorScheme.onSurface
                                    },
                                    fontWeight = if (isSelected || isTodayDate) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
