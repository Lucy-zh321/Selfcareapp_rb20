package com.example.selfcare

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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.selfcare.ui.theme.SelfCareTheme
import java.util.Calendar
import java.util.Locale

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

    val timeSlots = (0..23).map { "$it:00" }
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
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left arrow
//            IconButton(
//                onClick = { weekOffset-- },
//                modifier = Modifier.weight(0.1F)
//            ) {
//                Icon(
//                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                    contentDescription = "Previous Week",
//                    modifier = Modifier.padding(0.dp),
//                )
//            }

            Box(modifier = Modifier.weight(0.1F))

            // Day headers
            AnimatedContent(
                modifier = Modifier.weight(0.9F),
                targetState = weekDates,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith
                            fadeOut(animationSpec = tween(300))
                }
            ) { animatedWeekDates ->
                Row(
                    horizontalArrangement = Arrangement.SpaceAround) {
                    animatedWeekDates.forEachIndexed { index, (date, month, year) ->
                        val isToday = date == today.get(Calendar.DAY_OF_MONTH) &&
                                month == today.get(Calendar.MONTH) &&
                                year == today.get(Calendar.YEAR)

                        Column(
                            modifier = Modifier
                                .width(30.dp) // Fixed width for each day column
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
//            IconButton(
//                onClick = { weekOffset++ },
//                modifier = Modifier.weight(0.1F)
//            ) {
//                Icon(
//                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
//                    contentDescription = "Next Week"
//                )
//            }
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
            Column (
                modifier = Modifier.weight(0.1F),
                horizontalAlignment = Alignment.CenterHorizontally){
                timeSlots.forEach { timeLabel ->
                    Box(
                        modifier = Modifier
                            .height(60.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Text(
                            text = timeLabel,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }

            // Box for the grid that contains the vertical and horizontal lines
            Box(
                modifier = Modifier
                    .weight(0.9f)
                    .clip(MaterialTheme.shapes.medium)
                    .background(Color(0xFFFFF0F4)) // Light pink background
                    .border(1.dp, Color(0xFFB0B0B0), MaterialTheme.shapes.medium) // Outer border
                    .padding(0.dp) // Padding inside to keep lines from touching the outer box
            ) {
                // Horizontal lines (time slot separators)
                Column {
                    repeat(timeSlots.size) { index ->
                        // Vertical lines (day separators) for 7 columns (6 dividers)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(59.dp)
                                .padding(top = 0.dp), // Add top padding to give space
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            repeat(shortDayNames.size + 1) { // Repeat for 6 dividers to make 7 columns
                                VerticalDivider(
                                    color = Color(0xFFCCCCCC), // Vertical line color
                                    thickness = 1.dp, // Vertical line thickness
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .width(1.dp) // Make vertical lines 1 dp wide
                                        .padding(horizontal = 0.dp) // Padding between the lines
                                )
                            }
                        }
                        HorizontalDivider(
                            color = Color(0xFFCCCCCC), // Horizontal separator color
                            thickness = 1.dp,
                            modifier = Modifier
                                .height(1.dp)
                        )
                    }
                }
            }
//            Spacer (modifier = Modifier.weight(0.1F))
        }
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewCalendar() {
    SelfCareTheme {
        CalendarView()
    }
}