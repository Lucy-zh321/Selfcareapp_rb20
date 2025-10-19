package com.example.selfcare



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
    var isExpanded by remember { mutableStateOf(false) }

    // Calculate dynamic height based on content
    val dynamicHeight by animateDpAsState(
        targetValue = calculateSheetHeight(subtasks.size),
        label = "sheet_height_animation"
    )

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
                .heightIn(min = dynamicHeight)
                .padding(horizontal = 16.dp)
        ) {
            // Task Name Section
            Text(
                text = task.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Divider line
            Divider(
                color = Color.LightGray,
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Subtasks Section - Scrollable if needed
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(scrollState)
            ) {
                if (subtasks.isNotEmpty()) {
                    subtasks.forEach { subtask ->
                        SubtaskRow(
                            subtask = subtask,
                            selectedColor = task.color,
                            onTextChanged = { /* Not editable in detail view */ },
                            onCompletedChanged = { completed ->
                                onSubtaskChecked(subtask.id, completed)
                            },
                            onDelete = { /* Not deletable in detail view */ },
                            isEditable = false // Make it read-only in detail view
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                } else {
                    // Empty state
                    Text(
                        text = "No subtasks",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Delete Button
                OutlinedButton(
                    onClick = onDeleteTask,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .padding(end = 8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Red
                    ),
                    border = BorderStroke(1.dp, Color.Red)
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
                        containerColor = getSelectedColor(task.color)
                    )
                ) {
                    Text("Edit")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}


// Helper function to calculate dynamic height
private fun calculateSheetHeight(subtaskCount: Int): Dp {
    val baseHeight = 200.dp // Minimum height for task name + buttons
    val subtaskHeight = 48.dp // Height per subtask
    val maxHeight = 500.dp // Maximum height before scrolling

    val calculatedHeight = baseHeight + (subtaskCount * subtaskHeight)
    return minOf(calculatedHeight, maxHeight)
}