package com.example.selfcare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.selfcare.ui.theme.SelfCareTheme
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.with
import androidx.compose.runtime.*
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.animation.*
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.animation.with




import androidx.compose.material3.Text
import androidx.compose.material3.IconButton




class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SelfCareTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { BottomNavBar() } // 👈 Add this line for the bottom bar
                ) { innerPadding ->
                    CalendarView(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun CalendarView(modifier: Modifier = Modifier) {
    var weekOffset by remember { mutableStateOf(0) }

    val calendar = java.util.Calendar.getInstance()
    calendar.add(java.util.Calendar.WEEK_OF_YEAR, weekOffset)

    val currentYear = calendar.get(java.util.Calendar.YEAR)
    val currentMonth = calendar.getDisplayName(java.util.Calendar.MONTH, java.util.Calendar.LONG, java.util.Locale.getDefault())

    val today = java.util.Calendar.getInstance()

    // Get Monday of this week
    val dayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK)
    val mondayOffset = if (dayOfWeek == java.util.Calendar.SUNDAY) -6 else java.util.Calendar.MONDAY - dayOfWeek
    calendar.add(java.util.Calendar.DATE, mondayOffset)

    val weekDates = (0..6).map {
        val date = calendar.get(java.util.Calendar.DAY_OF_MONTH)
        val month = calendar.get(java.util.Calendar.MONTH)
        val year = calendar.get(java.util.Calendar.YEAR)
        calendar.add(java.util.Calendar.DATE, 1)
        Triple(date, month, year)
    }

    val timeSlots = (1..23).map { "$it :00" }
    val scrollState = rememberScrollState()
    val shortDayNames = listOf("M","T","W","T","F","S","S")

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

        // Days of Week
        // Days of Week Header with arrows
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left arrow
            IconButton(onClick = { weekOffset-- }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Previous Week"
                )
            }

            // Day columns (M, T, W… with date)
            AnimatedContent(
                targetState = weekDates,
                transitionSpec = {
                    // Slide in from left and fade in
                    slideInHorizontally(
                        animationSpec = tween(300),
                        initialOffsetX = { fullWidth -> if (weekOffset > 0) fullWidth else -fullWidth }
                    ) with fadeIn(animationSpec = tween(300)) // Combine slide in with fade in

                    // Slide out to the right and fade out
                    slideOutHorizontally(
                        animationSpec = tween(300),
                        targetOffsetX = { fullWidth -> if (weekOffset > 0) -fullWidth else fullWidth }
                    ) with fadeOut(animationSpec = tween(300)) // Combine slide out with fade out
                }
            ) { animatedWeekDates ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    animatedWeekDates.forEachIndexed { index, (date, month, year) ->
                        val isToday = date == today.get(java.util.Calendar.DAY_OF_MONTH)
                                && month == today.get(java.util.Calendar.MONTH)
                                && year == today.get(java.util.Calendar.YEAR)

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = shortDayNames[index],
                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                            )
                            Text(
                                text = date.toString(),
                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // Right arrow
            IconButton(onClick = { weekOffset++ }) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Next Week"
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Time Grid
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
                        .height(60.dp)
                ) {
                    // Fixed-width column for time label
                    Box(
                        modifier = Modifier
                            .width(50.dp)
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
    // Your bottom navigation implementation
}


@Preview(showBackground = true)
@Composable
fun PreviewCalendar() {
    SelfCareTheme {
        CalendarView()
    }
}
