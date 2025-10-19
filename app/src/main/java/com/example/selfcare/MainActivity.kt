package com.example.selfcare

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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


@Composable
fun MainScreen() {
    var currentScreen by remember { mutableStateOf("calendar") }
    var tasks by remember { mutableStateOf<List<Task>>(emptyList()) }

    when (currentScreen) {
        "calendar" -> {
            CalendarView(
                tasks = tasks,
                onAddTask = { currentScreen = "add_task" }, // This is called when your FAB is clicked
                modifier = Modifier.fillMaxSize()
            )
        }
        "add_task" -> {
            AddTaskScreen(
                onBack = { currentScreen = "calendar" },
                onTaskAdded = { newTask ->
                    // Add the new task to our list
                    tasks = tasks + newTask
                    // Go back to calendar
                    currentScreen = "calendar"
                }
            )
        }
    }
}


