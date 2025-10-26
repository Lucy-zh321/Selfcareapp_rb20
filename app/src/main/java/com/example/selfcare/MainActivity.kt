package com.example.selfcare

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.selfcare.ui.theme.SelfCareTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SelfCareTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen() // This calls your MainScreen composable
                }
            }
        }
    }
}


// Replace your current CalendarView call with:

// MainScreen.kt

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen() {
    var currentScreen by remember { mutableStateOf("calendar") }
    var tasks by remember { mutableStateOf<List<Task>>(emptyList()) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }

    when (currentScreen) {
        "calendar" -> {
            CalendarView(
                tasks = tasks,
                onAddTask = {
                    taskToEdit = null
                    currentScreen = "add_task"
                },
                onUpdateTask = { updatedTask ->
                    // Properly update the task in the list
                    tasks = tasks.map { task ->
                        if (task.id == updatedTask.id) {
                            updatedTask
                        } else {
                            task
                        }
                    }
                },
                onDeleteTask = { taskToDelete, deleteAllRepeats ->
                    if (deleteAllRepeats) {
                        tasks = tasks.filterNot {
                            it.id == taskToDelete.id ||
                                    it.name == taskToDelete.name && it.startTime == taskToDelete.startTime
                        }
                    } else {
                        tasks = tasks.filterNot { it.id == taskToDelete.id }
                    }
                },
                onEditTask = { taskToEditParam ->
                    taskToEdit = taskToEditParam
                    currentScreen = "add_task"
                }
            )
        }
        "add_task" -> {
            AddTaskScreen(
                onBack = { currentScreen = "calendar" },
                onTaskAdded = { newTask ->
                    if (taskToEdit != null) {
                        // UPDATE EXISTING TASK
                        tasks = tasks.map { if (it.id == newTask.id) newTask else it }
                        taskToEdit = null
                    } else {
                        // ADD NEW TASK
                        tasks = tasks + newTask
                    }
                    currentScreen = "calendar"
                },
                existingTask = taskToEdit
            )
        }
    }
}

