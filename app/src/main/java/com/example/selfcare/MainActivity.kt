package com.example.selfcare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.selfcare.ui.theme.SelfCareTheme
import java.util.*
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape

import androidx.compose.material3.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.ui.draw.clip










class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SelfCareTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { BottomNavBar() }
                ) { innerPadding ->
                    CalendarView(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CalendarView(modifier: Modifier = Modifier) {
    var weekOffset by remember { mutableStateOf(0) }

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

    val timeSlots = (1..23).map { "$it:00" }
    val scrollState = rememberScrollState()
    val shortDayNames = listOf("M", "T", "W", "T", "F", "S", "S")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .pointerInput(Unit) {
                detectHorizontalDragGestures { _, dragAmount ->
                    if (dragAmount > 50) {
                        weekOffset-- // swipe right -> previous week
                    } else if (dragAmount < -50) {
                        weekOffset++ // swipe left -> next week
                    }
                }
            }
    ) {
        // Month and Year
        Text(
            text = "$currentMonth $currentYear",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        // Days of Week Header with arrows
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left arrow
            IconButton(onClick = { weekOffset-- }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Previous Week"
                )
            }

            // Day headers
            AnimatedContent(
                targetState = weekDates,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) with
                            fadeOut(animationSpec = tween(300))
                }
            ) { animatedWeekDates ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    animatedWeekDates.forEachIndexed { index, (date, month, year) ->
                        val isToday = date == today.get(Calendar.DAY_OF_MONTH) &&
                                month == today.get(Calendar.MONTH) &&
                                year == today.get(Calendar.YEAR)

                        Column(
                            modifier = Modifier
                                .width(50.dp) // Fixed width for each day column
                                .padding(horizontal = 2.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = shortDayNames[index],
                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                            )
                            Box(
                                modifier = Modifier
                                    .size(24.dp) // Set a fixed size for the circle
                                    .clip(CircleShape)
                                    .background(
                                        if (isToday) Color(0xFFF9C8D9) else Color.Transparent
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = date.toString(),
                                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }

                    }
                }
            }

            // Right arrow
            IconButton(onClick = { weekOffset++ }) {
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = "Next Week"
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Time Grid
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
        ) {
            timeSlots.forEach { timeLabel ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(65.dp)
                ) {
                    // Fixed-width column for time label
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = timeLabel,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    // 7 columns for the days
                    repeat(7) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(2.dp)
                                .background(MaterialTheme.colorScheme.secondary)
                        )
                    }
                }
            }
        }
    }
}




@Composable
fun BottomNavBar() {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.DateRange, contentDescription = "Back") },
            label = { Text("Back") },
            selected = false,
            onClick = { /* Handle click */ }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.FavoriteBorder, contentDescription = "Health") },
            label = { Text("Next") },
            selected = false,
            onClick = { /* Handle click */ }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Lock, contentDescription = "Financials") },
            label = { Text("Settings") },
            selected = false,
            onClick = { /* Handle click */ }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCalendar() {
    SelfCareTheme {
        CalendarView()
    }
}
