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
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.border



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

@Composable
fun CalendarView(modifier: Modifier = Modifier) {
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

    val timeSlots = (1..23).map { "$it:00" }
    val scrollState = rememberScrollState()
    val shortDayNames = listOf("M", "T", "W", "T", "F", "S", "S")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(0.dp)
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
            modifier = Modifier.fillMaxWidth().padding(horizontal = 0.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Left arrow
            IconButton(
                onClick = { weekOffset-- },
                modifier = Modifier.padding(0.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Previous Week",
                    modifier = Modifier.padding(0.dp),
                )
            }

            // Day headers
            AnimatedContent(
                modifier = Modifier.fillMaxWidth(0.9F),
                targetState = weekDates,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith
                            fadeOut(animationSpec = tween(300))
                }
            ) { animatedWeekDates ->
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly) {
                    animatedWeekDates.forEachIndexed { index, (date, month, year) ->
                        val isToday = date == today.get(Calendar.DAY_OF_MONTH) &&
                                month == today.get(Calendar.MONTH) &&
                                year == today.get(Calendar.YEAR)

                        Column(
                            modifier = Modifier
                                .width(40.dp) // Fixed width for each day column
                                .padding(horizontal = 0.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = shortDayNames[index],
                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                            )
                            Box(
                                modifier = Modifier
                                    .size(30.dp) // Set a fixed size for the circle
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
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next Week"
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Time Grid

        // Time + Grid section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp)
                .verticalScroll(scrollState)
        ) {
            // Time labels
            Column {
                timeSlots.forEach { timeLabel ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.1F)
                            .height(60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = timeLabel,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // Box for the grid that contains the vertical and horizontal lines
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(MaterialTheme.shapes.medium)
                    .background(Color(0xFFFFF0F4)) // Light pink background
                    .border(1.dp, Color(0xFFB0B0B0), MaterialTheme.shapes.medium) // Outer border
                    .padding(0.dp) // Padding inside to keep lines from touching the outer box
                    .fillMaxWidth(0.9F)
            ) {
                // Horizontal lines (time slot separators)
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    repeat(timeSlots.size) { index ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            HorizontalDivider(
                                color = Color(0xFFCCCCCC), // Horizontal separator color
                                thickness = 1.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                            )
                        }

                        // Vertical lines (day separators) for 7 columns (6 dividers)
                        Row(
                            modifier = Modifier
                                .height(59.dp)
                                .padding(top = 0.dp) // Add top padding to give space
                        ) {
                            repeat(shortDayNames.size - 1) { // Repeat for 6 dividers to make 7 columns
                                Text(
                                    text = "  ",
                                    modifier = Modifier
                                        .size(50.dp)
                                        .padding(bottom = 8.dp)
                                )
                                VerticalDivider(
                                    color = Color(0xFFCCCCCC), // Vertical line color
                                    thickness = 1.dp, // Vertical line thickness
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .width(1.dp) // Make vertical lines 1 dp wide
                                        .padding(horizontal = 2.dp) // Padding between the lines
                                )
                            }

                            Text(
                                text = "  ",
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(bottom = 8.dp)
                            )
                        }
//                        Spacer(modifier = Modifier.height(60.dp)) // Height for each time slot row
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
