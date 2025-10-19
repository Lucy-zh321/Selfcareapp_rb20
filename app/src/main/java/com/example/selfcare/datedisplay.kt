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
import org.threeten.bp.format.TextStyle
import androidx. compose. foundation. background
import java.util.Locale
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import org.threeten.bp.YearMonth
import androidx. compose. foundation. pager. rememberPagerState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    initialDate: org.threeten.bp.LocalDate = org.threeten.bp.LocalDate.now(),
    onDateSelected: (org.threeten.bp.LocalDate) -> Unit,
    onDismiss: () -> Unit,
    selectedColor: Color? = null
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
        modifier = Modifier.width(500.dp),
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
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
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
                        Box(
                            modifier = Modifier
                                .weight(1f) // Same weight distribution
                                .aspectRatio(1f), // Same aspect ratio
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
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
                        currentMonth = month.atDay(1),  // Convert YearMonth to LocalDate by adding day 1
                        selectedDay = selectedDay,
                        onDaySelected = { date ->
                            selectedDay = date.dayOfMonth  // Extract day from LocalDate
                        },
                        selectedColor = selectedColor
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
    currentMonth: LocalDate,
    selectedDay: Int,
    onDaySelected: (LocalDate) -> Unit,
    selectedColor: Color? = null
) {
    val firstDayOfMonth = LocalDate.of(currentMonth.year, currentMonth.monthValue, 1)
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // Sunday=0
    val daysInMonth = firstDayOfMonth.lengthOfMonth()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
    ) {
        // Calendar grid with 6 rows (maximum weeks in month)
        repeat(6) { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(7) { dayOfWeek ->
                    val dayIndex = week * 7 + dayOfWeek
                    val isEmptyCell = dayIndex < firstDayOfWeek || dayIndex >= firstDayOfWeek + daysInMonth

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                    ) {
                        if (isEmptyCell) {
                            // Empty cell for days outside current month
                            Box(modifier = Modifier.fillMaxSize())
                        } else {
                            val dayNumber = dayIndex - firstDayOfWeek + 1
                            val currentDate = LocalDate.of(currentMonth.year, currentMonth.monthValue, dayNumber)
                            val isSelected = currentDate.dayOfMonth == selectedDay
                            val isToday = currentDate == LocalDate.now()

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(3.dp), // Reduced padding from 4.dp to 3.dp to give more space for circle
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp) // Increased from 28.dp to 32.dp
                                        .background(
                                            color = when {
                                                isSelected -> getSelectedColor(selectedColor)
                                                isToday -> getSelectedColorWithAlpha(selectedColor, 0.3f)
                                                else -> Color.Transparent
                                            },
                                            shape = CircleShape
                                        )
                                        .border(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) getSelectedColor(selectedColor)
                                            else if (isToday) getSelectedColor(selectedColor).copy(alpha = 0.5f)
                                            else Color.LightGray.copy(alpha = 0.3f),
                                            shape = CircleShape
                                        )
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) { onDaySelected(currentDate) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = dayNumber.toString(),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontSize = 15.sp, // Slightly increased from 14.sp to 15.sp
                                        color = when {
                                            isSelected -> Color.White
                                            isToday -> getSelectedColor(selectedColor)
                                            else -> MaterialTheme.colorScheme.onBackground
                                        },
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}