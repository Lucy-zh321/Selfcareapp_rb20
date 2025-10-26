package com.example.selfcare



import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.min
import com.example.selfcare.Task
import com.example.selfcare.SubtaskItem
import com.example.selfcare.getSelectedColor
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.times
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailBottomSheet(
    task: Task,
    subtasks: List<SubtaskItem>,
    onSubtaskChecked: (Int, Boolean) -> Unit,
    onDeleteTask: () -> Unit,
    onEditTask: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier,
        containerColor = Color.White,
        dragHandle = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp),
                contentAlignment = Alignment.Center
            ) {
                // Drag handle
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.Gray.copy(alpha = 0.5f))
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Time and Date Section - ADD THIS SECTION
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                // Format the date for display
                val formattedDate = try {
                    val taskDate = LocalDate.parse(task.date)
                    taskDate.format(DateTimeFormatter.ofPattern("EEE, MMM d, yyyy"))
                } catch (e: Exception) {
                    task.date // Fallback to raw date if parsing fails
                }

                // Time range
                Text(
                    text = "${task.startTime} - ${task.endTime}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth()
                )

                // Date
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Task Name Section
            Text(
                text = task.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // Divider line
            Divider(
                color = Color.LightGray,
                thickness = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // Subtasks Section
            if (subtasks.isNotEmpty()) {
                Text(
                    text = "Subtasks",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                subtasks.forEach { subtask ->
                    SubtaskCheckRow(
                        subtask = subtask,
                        taskColor = task.color,
                        onCheckedChange = { checked ->
                            onSubtaskChecked(subtask.id, checked)
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Details Section - ADD THIS SECTION
            if (task.details.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "Details",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = task.details,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(
                                Color.LightGray.copy(alpha = 0.2f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Action Buttons Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Delete Button
                Button(
                    onClick = onDeleteTask,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .padding(end = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = task.color,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Delete")
                }

                // Edit Button
                Button(
                    onClick = onEditTask,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .padding(start = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = task.color,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Edit")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun SubtaskCheckRow(
    subtask: SubtaskItem,
    taskColor: Color,
    onCheckedChange: (Boolean) -> Unit
) {
    var currentCheckedState by remember(subtask.id) {
        mutableStateOf(subtask.isCompleted)
    }

    LaunchedEffect(subtask.isCompleted) {
        currentCheckedState = subtask.isCompleted
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val newState = !currentCheckedState
                currentCheckedState = newState
                onCheckedChange(newState)
            }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = currentCheckedState,
            onCheckedChange = { newState ->
                currentCheckedState = newState
                onCheckedChange(newState)
            },
            colors = CheckboxDefaults.colors(
                checkedColor = taskColor,
                uncheckedColor = taskColor.copy(alpha = 0.6f)
            ),
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = subtask.text,
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 4.dp),
            style = MaterialTheme.typography.bodyMedium.copy(
                textDecoration = if (currentCheckedState) TextDecoration.LineThrough else TextDecoration.None
            ),
            color = if (currentCheckedState) Color.Gray else Color.Black
        )
    }
}

@Composable
fun DeleteTaskDialog(
    task: Task,
    onDismiss: () -> Unit,
    onDeleteThisTask: () -> Unit,
    onDeleteAllRepeats: () -> Unit,
    taskColor: Color
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Delete Task", fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                Text("How would you like to delete this task?")
                Spacer(modifier = Modifier.height(16.dp))

                if (task.repeatRule != null) {
                    // Show both options for repeating tasks
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onDeleteThisTask() }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = false,
                            onClick = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Delete only this occurrence", fontWeight = FontWeight.Medium)
                            Text("Remove this task only for ${task.date}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray)
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onDeleteAllRepeats() }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = false,
                            onClick = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Delete all repeats", fontWeight = FontWeight.Medium)
                            Text("Remove this task and all future occurrences",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray)
                        }
                    }
                } else {
                    // Single task - just show delete option
                    Text("This task will be permanently deleted.")
                }
            }
        },
        confirmButton = {
            if (task.repeatRule != null) {
                // For repeating tasks, show both options as buttons
                Row {
                    TextButton(
                        onClick = onDeleteThisTask,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = taskColor
                        )
                    ) {
                        Text("This occurrence only")
                    }
                    TextButton(
                        onClick = onDeleteAllRepeats,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = taskColor
                        )
                    ) {
                        Text("All repeats")
                    }
                }
            } else {
                // For single tasks, just show delete
                TextButton(
                    onClick = onDeleteThisTask,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = taskColor
                    )
                ) {
                    Text("Delete")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


// Helper function to calculate dynamic height
private fun calculateSheetHeight(subtaskCount: Int): Dp {
    val baseHeight = 200.dp // Minimum height for task name + buttons
    val subtaskHeight = 48.dp // Height per subtask
    val maxHeight = 500.dp // Maximum height before scrolling

    val calculatedHeight = baseHeight + (subtaskCount * subtaskHeight)
    return minOf(calculatedHeight, maxHeight)
}