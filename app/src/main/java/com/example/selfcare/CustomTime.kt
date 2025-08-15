package com.example.selfcare



import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType

sealed class EndCondition {
    object Never : EndCondition()
    data class OnDate(val date: LocalDate) : EndCondition()
    data class AfterOccurrences(val count: Int) : EndCondition()
}

@Composable
fun CustomRepeatDialog(
    initialInterval: Int,
    initialUnit: String,
    initialDays: Set<Int>,
    initialEndCondition: EndCondition,
    onDismiss: () -> Unit,
    onConfirm: (interval: Int, unit: String, days: Set<Int>, endCondition: EndCondition) -> Unit
) {
    // State management
    var interval by remember { mutableStateOf(1) }
    var selectedUnit by remember { mutableStateOf("Week") }
    val selectedDays = remember { mutableStateSetOf<Int>() }
    var endCondition by remember { mutableStateOf<EndCondition>(EndCondition.Never) }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember {
        mutableStateOf(
            when (initialEndCondition) {
                is EndCondition.OnDate -> initialEndCondition.date
                else -> LocalDate.now()
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Custom Repeat",
                style = MaterialTheme.typography.titleLarge.copy(
                )
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                // Frequency Section
                Text(
                    text = "Repeats every:",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    OutlinedTextField(
                        value = interval.toString(),
                        onValueChange = { newValue ->
                            interval = newValue.filter { it.isDigit() }.take(2).toIntOrNull() ?: 1
                        },
                        modifier = Modifier.width(60.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color(0xFFE91E63),
                            unfocusedIndicatorColor = Color.LightGray
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    UnitDropdown(
                        selectedUnit = selectedUnit,
                        onUnitSelected = { selectedUnit = it }
                    )
                }

                // Days of Week Section
                Text(
                    text = "Repeats on:",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                WeekdaySelector(
                    selectedDays = selectedDays,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // End Condition Section
                Text(
                    text = "Ends:",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                EndConditionSelector(
                    currentCondition = endCondition,
                    selectedDate = selectedDate,
                    onConditionSelected = { endCondition = it },
                    onDateClicked = { showDatePicker = true },
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(interval, selectedUnit, selectedDays, endCondition)
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFFFFE0E0)
                )
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.Gray
                )
            ) {
                Text("Cancel")
            }
        }
    )

    if (showDatePicker) {
        DatePickerDialog(
            initialDate = selectedDate,
            onDateSelected = { date ->
                selectedDate = date
                endCondition = EndCondition.OnDate(date)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

@Composable
private fun UnitDropdown(
    selectedUnit: String,
    onUnitSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val units = listOf("Day", "Week", "Month", "Year")

    Box(modifier = Modifier.width(100.dp)) {
        OutlinedButton(
            onClick = { expanded = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.Black
            ),
            border = BorderStroke(1.dp, Color.Gray),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(selectedUnit)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Select unit"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            units.forEach { unit ->
                DropdownMenuItem(
                    text = { Text(unit) },
                    onClick = {
                        onUnitSelected(unit)
                        expanded = false
                    }
                )
            }
        }
    }
}
@Composable
private fun WeekdaySelector(
    selectedDays: MutableSet<Int>,
    modifier: Modifier = Modifier
) {
    val days = listOf("M", "T", "W", "T", "F", "S", "S")

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth()
    ) {
        days.forEachIndexed { index, day ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 2.dp)
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(
                        if (selectedDays.contains(index)) Color(0xFFFFE0E0)
                        else Color.Transparent
                    )
                    .border(
                        1.dp,
                        if (selectedDays.contains(index)) Color(0xFFFFE0E0)
                        else Color.LightGray,
                        CircleShape
                    )
                    .clickable {
                        if (selectedDays.contains(index)) {
                            selectedDays.remove(index)
                        } else {
                            selectedDays.add(index)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day,
                    color = if (selectedDays.contains(index)) Color.Black else Color.Black,
                    fontWeight = if (selectedDays.contains(index)) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}
@Composable
private fun DayPill(
    day: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .aspectRatio(1f) // Ensures perfect circle
            .clip(CircleShape)
            .background(if (selected) Color(0xFFFFE0E0) else Color.Transparent)
            .border(
                1.dp,
                if (selected) Color(0xFFFFE0E0) else Color.LightGray,
                CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day,
            color = if (selected) Color.White else Color.Black,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun EndConditionSelector(
    currentCondition: EndCondition,
    selectedDate: LocalDate,
    onConditionSelected: (EndCondition) -> Unit,
    onDateClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            RadioButton(
                selected = currentCondition is EndCondition.Never,
                onClick = { onConditionSelected(EndCondition.Never) }
            )
            Text("Never", modifier = Modifier.padding(start = 8.dp))
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            RadioButton(
                selected = currentCondition is EndCondition.OnDate,
                onClick = { onConditionSelected(EndCondition.OnDate(selectedDate)) }
            )
            Text("On", modifier = Modifier.padding(start = 8.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                color = if (currentCondition is EndCondition.OnDate) Color(0xFFFFE0E0) else Color.Black,
                modifier = Modifier.clickable(onClick = onDateClicked)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            RadioButton(
                selected = currentCondition is EndCondition.AfterOccurrences,
                onClick = {
                    val currentCount = (currentCondition as? EndCondition.AfterOccurrences)?.count ?: 1
                    onConditionSelected(EndCondition.AfterOccurrences(currentCount))
                }
            )
            Text("After", modifier = Modifier.padding(start = 8.dp))
            Spacer(modifier = Modifier.width(8.dp))
            val occurrenceCount = remember(currentCondition) {
                (currentCondition as? EndCondition.AfterOccurrences)?.count ?: 1
            }
            OutlinedTextField(
                value = when (currentCondition) {
                    is EndCondition.AfterOccurrences -> currentCondition.count.toString()
                    else -> "1"
                },
                onValueChange = { newValue ->
                    val count = newValue.filter { it.isDigit() }.take(2).toIntOrNull() ?: 1
                    onConditionSelected(EndCondition.AfterOccurrences(count))
                },
                modifier = Modifier.width(60.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = if (currentCondition is EndCondition.AfterOccurrences) {
                        Color(0xFFFFE0E0)
                    } else {
                        Color.LightGray
                    }
                )
            )
            Text("occurrences", modifier = Modifier.padding(start = 8.dp))
        }
    }
}