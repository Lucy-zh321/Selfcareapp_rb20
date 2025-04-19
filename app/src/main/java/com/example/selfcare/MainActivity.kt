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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.filled.Add
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.rememberLazyListState
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import kotlin.math.max
import kotlin.math.min





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
fun TaskDialog(
    taskName: String,
    taskTime: String,
    onTaskNameChange: (String) -> Unit,
    onTaskTimeChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Task") },
        text = {
            Column {
                Text("Task Name:")
                TextField(
                    value = taskName,
                    onValueChange = onTaskNameChange,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Time:")
                TextField(
                    value = taskTime,
                    onValueChange = onTaskTimeChange,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onSave) {
                Text("Save")
            }
        }
    )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(onBack: () -> Unit) {
    var taskName by remember { mutableStateOf("") }
    var startHour by remember { mutableStateOf(12) }
    var startMinute by remember { mutableStateOf(0) }
    var endHour by remember { mutableStateOf(13) }
    var endMinute by remember { mutableStateOf(0) }
    var startTime by remember { mutableStateOf("12:00") }
    var endTime by remember { mutableStateOf("13:00") }
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

            Spacer(modifier = Modifier.height(16.dp))

            // Row with two empty boxes side by side
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp) // Adjust spacing between boxes
            ) {
                // Left box (Start)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Start", style = MaterialTheme.typography.bodySmall)
                    Box(
                        modifier = Modifier
                            .height(80.dp)
                            .fillMaxWidth() // Ensure the box fills the available width
                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                    ){
                        // Simple text field for the Start time (e.g., "12:30")
                        OutlinedTextField(
                            value = startTime,
                            onValueChange = { newTime ->
                                startTime = newTime
                            },
                            label = { Text("Start Time") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }









                }

                // Right box (End)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "End", style = MaterialTheme.typography.bodySmall)
                    Box(
                        modifier = Modifier
                            .height(80.dp)
                            .fillMaxWidth() // Ensure the box fills the available width

                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                    )
                }
            }
        }
    }
}


//@Composable
//fun TimePickerBox(
//    label: String,
//    hour: Int,
//    minute: Int,
//    onHourChange: (Int) -> Unit,
//    onMinuteChange: (Int) -> Unit
//) {
//    val hourList = (0..23).map { it.toString().padStart(2, '0') }
//    val minuteList = (0..59).map { it.toString().padStart(2, '0') }
//    val listState = rememberLazyListState()
//
//    Column(
//        modifier = Modifier
//            .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(8.dp))
//            .padding(16.dp)
//    ) {
//        Text(text = label, style = MaterialTheme.typography.bodyLarge)
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        Column(
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Text(text = hourList[hour], style = MaterialTheme.typography.headlineSmall)
//
//            // Faded hour and minute values
//            Row(
//                horizontalArrangement = Arrangement.Center,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    text = hourList[max(0, hour - 1)],
//                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
//                )
//                Spacer(modifier = Modifier.width(4.dp))
//                Text(text = ":", style = MaterialTheme.typography.bodySmall)
//                Spacer(modifier = Modifier.width(4.dp))
//                Text(
//                    text = hourList[min(23, hour + 1)],
//                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
//                )
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Minute picker
//            Text(text = minuteList[minute], style = MaterialTheme.typography.headlineSmall)
//
//            // Faded minute values
//            Row(
//                horizontalArrangement = Arrangement.Center,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    text = minuteList[max(0, minute - 1)],
//                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
//                )
//                Spacer(modifier = Modifier.width(4.dp))
//                Text(text = ":", style = MaterialTheme.typography.bodySmall)
//                Spacer(modifier = Modifier.width(4.dp))
//                Text(
//                    text = minuteList[min(59, minute + 1)],
//                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
//                )
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Lazy Column for scrolling hours and minutes
//            LazyColumn(state = listState) {
//                itemsIndexed(hourList) { index, item ->
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .clickable {
//                                onHourChange(index)
//                            }
//                            .padding(8.dp),
//                        horizontalArrangement = Arrangement.Center
//                    ) {
//                        Text(text = item, style = MaterialTheme.typography.bodyLarge)
//                    }
//                }
//            }
//        }
//    }
//}

//@Composable
//fun TimePickerBox(
//    label: String,
//    selectedHour: Int,
//    selectedMinute: Int,
//    onHourChange: (Int) -> Unit,
//    onMinuteChange: (Int) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    val hours = (0..23).toList()
//    val minutes = (0..59).toList()
//
//    Column(
//        modifier = Modifier
//            .height(180.dp)
//            .clip(RoundedCornerShape(16.dp))
//            .background(Color(0xFFF3F3F3))
//            .padding(8.dp)
//    ) {
//        Text(
//            text = label,
//            style = MaterialTheme.typography.labelLarge,
//            modifier = Modifier.padding(bottom = 4.dp)
//        )
//        Divider(color = Color.Gray, thickness = 1.dp)
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceEvenly
//        ) {
//            TimeScrollColumn(
//                items = hours,
//                selectedItem = selectedHour,
//                onSelectedItemChange = onHourChange
//            )
//            Text(":", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.align(Alignment.CenterVertically))
//            TimeScrollColumn(
//                items = minutes,
//                selectedItem = selectedMinute,
//                onSelectedItemChange = onMinuteChange
//            )
//        }
//        Divider(color = Color.Gray, thickness = 1.dp)
//    }
//}

@Composable
fun TimeScrollColumn(
    items: List<Int>,
    selectedItem: Int,
    onSelectedItemChange: (Int) -> Unit
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(selectedItem) {
        coroutineScope.launch {
            listState.scrollToItem(selectedItem)
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .height(100.dp)
            .width(60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(items.size) { index ->
            val isSelected = index == selectedItem
            Text(
                text = items[index].toString().padStart(2, '0'),
                style = if (isSelected) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                modifier = Modifier
                    .height(32.dp)
                    .clickable {
                        onSelectedItemChange(index)
                    },
                textAlign = TextAlign.Center
            )
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

