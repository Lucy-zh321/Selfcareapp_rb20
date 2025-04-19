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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.filled.Add
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.unit.*
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.platform.LocalDensity







class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SelfCareTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "main") {
                    composable("main") {
                        MainScreen(onAddTaskClick = {
                            navController.navigate("addTask")
                        })
                    }

                    composable("addTask") {
                        AddTaskScreen(onBack = {
                            navController.popBackStack()
                        })
                    }
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

            // Grid with tasks
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(MaterialTheme.shapes.medium)
                    .background(Color(0xFFFFF0F4)) // Light pink background
                    .border(1.dp, Color(0xFFB0B0B0), MaterialTheme.shapes.medium) // Outer border
                    .padding(0.dp) // Padding inside to keep lines from touching the outer box
                    .fillMaxWidth(0.9F)
            ) {
                // Vertical and horizontal lines for time slots
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

                        // Vertical lines (day separators)
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
                    }
                }
            }

            // Show task dialog if needed

        }
    }
}










@Composable
fun BottomNavBar() {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.DateRange, contentDescription = "Back") },
            label = { Text("Calendar") },
            selected = false,
            onClick = { /* Handle click */ }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.FavoriteBorder, contentDescription = "Health") },
            label = { Text("Health") },
            selected = false,
            onClick = { /* Handle click */ }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Lock, contentDescription = "Financials") },
            label = { Text("Financials") },
            selected = false,
            onClick = { /* Handle click */ }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(onBack: () -> Unit) {
    var taskName by remember { mutableStateOf("") }
    var startHour by remember { mutableIntStateOf(9) }
    var startMinute by remember { mutableIntStateOf(0) }
    var endHour by remember { mutableIntStateOf(13) }
    var endMinute by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("New Task") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            OutlinedTextField(
                value = taskName,
                onValueChange = { taskName = it },
                label = { Text("Task Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Start Time Picker
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Start", style = MaterialTheme.typography.labelMedium)
                    Box(
                        modifier = Modifier
                            .height(100.dp)
                            .fillMaxWidth()
                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        TimePickerWheel(
                            selectedHour = startHour,
                            selectedMinute = startMinute,
                            onHourChange = { startHour = it },
                            onMinuteChange = { startMinute = it }
                        )
                    }
                }

                // End Time Picker
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "End", style = MaterialTheme.typography.labelMedium)
                    Box(
                        modifier = Modifier
                            .height(100.dp)
                            .fillMaxWidth()
                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        TimePickerWheel(
                            selectedHour = endHour,
                            selectedMinute = endMinute,
                            onHourChange = { endHour = it },
                            onMinuteChange = { endMinute = it }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun TimePickerWheel(
    selectedHour: Int,
    selectedMinute: Int,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit
) {
    val visibleItems = 3
    val cellHeight = 30.dp
    val hourList = remember { (0..23).toList() }
    val minuteList = remember { (0..59).toList() }

    val repeatedHours = remember { List(100) { hourList }.flatten() }
    val repeatedMinutes = remember { List(100) { minuteList }.flatten() }

    val hourState = rememberLazyListState(50 * hourList.size + selectedHour)
    val minuteState = rememberLazyListState(50 * minuteList.size + selectedMinute)

    val isHourScrolling by remember { derivedStateOf { hourState.isScrollInProgress } }
    val isMinuteScrolling by remember { derivedStateOf { minuteState.isScrollInProgress } }
    var hourSelectedIndex by remember { mutableIntStateOf(-1) }
    var minuteSelectedIndex by remember { mutableIntStateOf(-1) }


    // Snap selection when scrolling stops
    // Get the hour that's visually centered
    LaunchedEffect(hourState.isScrollInProgress.not()) {
        val visibleItems = hourState.layoutInfo.visibleItemsInfo
        val center = hourState.layoutInfo.viewportSize.height / 2
        val centeredItem = visibleItems.minByOrNull {
            kotlin.math.abs((it.offset + it.size / 2) - center)
        }
        centeredItem?.let {
            hourSelectedIndex = it.index
            val hour = repeatedHours[it.index % repeatedHours.size]
            onHourChange(hour)
        }
    }



    LaunchedEffect(minuteState.isScrollInProgress.not()) {
        val visibleItems = minuteState.layoutInfo.visibleItemsInfo
        val center = minuteState.layoutInfo.viewportSize.height / 2
        val centeredItem = visibleItems.minByOrNull {
            kotlin.math.abs((it.offset + it.size / 2) - center)
        }
        centeredItem?.let {
            minuteSelectedIndex = it.index
            val minute = repeatedMinutes[it.index % repeatedMinutes.size]
            onMinuteChange(minute)
        }
    }



    val selectorLineColor = Color.Gray
    val selectorLineThickness = 1.dp

    Box(
        modifier = Modifier
            .height(cellHeight * visibleItems)
            .fillMaxWidth()
    ) {
        // Selector lines (top & bottom)
        Column(
            modifier = Modifier.matchParentSize()
        ) {
            Spacer(modifier = Modifier.height(cellHeight - 6.dp))
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = selectorLineThickness,
                color = selectorLineColor
            )
            Spacer(modifier = Modifier.height(cellHeight + 6.dp))
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = selectorLineThickness,
                color = selectorLineColor
            )
        }

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hour Wheel
            LazyColumn(
                state = hourState,
                modifier = Modifier.width(80.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                flingBehavior = rememberSnapFlingBehavior(hourState),
                contentPadding = PaddingValues(vertical = cellHeight)
            ) {
                items(repeatedHours.size) { index ->
                    val hour = repeatedHours[index]
                    val isSelected = index == hourSelectedIndex && !isHourScrolling
                    val animatedSize by animateDpAsState(
                        targetValue = if (isSelected) 24.dp else 20.dp,
                        label = "HourSize"
                    )
                    val animatedAlpha by animateFloatAsState(
                        targetValue = if (isSelected) 1f else 0.5f,
                        label = "HourAlpha"
                    )
                    Text(
                        text = hour.toString().padStart(2, '0'),
                        modifier = Modifier
                            .height(cellHeight)
                            .width(60.dp),
                        fontSize = with(LocalDensity.current) { animatedSize.toSp() },
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = Color.Black.copy(alpha = animatedAlpha),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Colon
            Text(
                text = ":",
                fontSize = 24.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            // Minute Wheel
            LazyColumn(
                state = minuteState,
                modifier = Modifier.width(80.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                flingBehavior = rememberSnapFlingBehavior(minuteState),
                contentPadding = PaddingValues(vertical = cellHeight)
            ) {
                items(repeatedMinutes.size) { index ->
                    val minute = repeatedMinutes[index]
                    val isSelected = index == minuteSelectedIndex && !isMinuteScrolling
                    val animatedSize by animateDpAsState(
                        targetValue = if (isSelected) 24.dp else 20.dp,
                        label = "MinuteSize"
                    )
                    val animatedAlpha by animateFloatAsState(
                        targetValue = if (isSelected) 1f else 0.5f,
                        label = "MinuteAlpha"
                    )
                    Text(
                        text = minute.toString().padStart(2, '0'),
                        modifier = Modifier
                            .height(cellHeight)
                            .width(60.dp),
                        fontSize = with(LocalDensity.current) { animatedSize.toSp() },
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = Color.Black.copy(alpha = animatedAlpha),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}







@Composable
fun MainScreen(onAddTaskClick: () -> Unit) {
    Scaffold(
        bottomBar = { BottomNavBar() },
        floatingActionButton = {
            FloatingActionButton(onClick = { onAddTaskClick() }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Task")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            CalendarView()
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

