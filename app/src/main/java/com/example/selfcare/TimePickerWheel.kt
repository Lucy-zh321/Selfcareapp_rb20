package com.example.selfcare

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.selfcare.ui.theme.SelfCareTheme

@Composable
fun TimePickerWheel(
    selectedHour: Int,
    selectedMinute: Int,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit
) {
    val visibleItems = 3
    val cellHeight = 24.dp
    val hourList = remember { (0..23).toList() }
    val minuteList = remember { (0..59).toList() }

    val repeatedHours = remember { List(100) { hourList }.flatten() }
    val repeatedMinutes = remember { List(100) { minuteList }.flatten() }

    val hourState = rememberLazyListState(50 * hourList.size + selectedHour)
    val minuteState = rememberLazyListState(50 * minuteList.size + selectedMinute)

    // Helper function to get centered index
    fun getCenteredItemIndex(state: LazyListState): Int? {
        val center = state.layoutInfo.viewportSize.height / 2
        return state.layoutInfo.visibleItemsInfo
            .minByOrNull { kotlin.math.abs((it.offset + it.size / 2) - center) }
            ?.index
    }


    // Get current centered items instantly
    val hourCenteredIndex = getCenteredItemIndex(hourState)
    val minuteCenteredIndex = getCenteredItemIndex(minuteState)

    // Update external selection when scroll stops
    LaunchedEffect(hourState.isScrollInProgress.not()) {
        hourCenteredIndex?.let {
            onHourChange(repeatedHours[it % repeatedHours.size])
        }
    }

    LaunchedEffect(minuteState.isScrollInProgress.not()) {
        minuteCenteredIndex?.let {
            onMinuteChange(repeatedMinutes[it % repeatedMinutes.size])
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
        Column(modifier = Modifier.matchParentSize()) {
            Spacer(modifier = Modifier.height(cellHeight))
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = selectorLineThickness,
                color = selectorLineColor
            )
            Spacer(modifier = Modifier.height(cellHeight))
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
                    val isSelected = index + 1 == hourCenteredIndex
                    val animatedSize by animateDpAsState(
                        targetValue = if (isSelected) 20.dp else 15.dp,
                        label = "HourSize"
                    )
                    val animatedAlpha by animateFloatAsState(
                        targetValue = if (isSelected) 1f else 0.5f,
                        label = "HourAlpha"
                    )
                    Box(modifier = Modifier.height(cellHeight), contentAlignment = Alignment.Center){
                        Text(
                            text = hour.toString().padStart(2, '0'),
                            modifier = Modifier.width(60.dp),
                            fontSize = with(LocalDensity.current) { animatedSize.toSp() },
                            color = Color.Black.copy(alpha = animatedAlpha),
                            textAlign = TextAlign.Center
                        )
                    }
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
                    val isSelected = index + 1 == minuteCenteredIndex
                    val animatedSize by animateDpAsState(
                        targetValue = if (isSelected) 20.dp else 15.dp,
                        label = "MinuteSize"
                    )
                    val animatedAlpha by animateFloatAsState(
                        targetValue = if (isSelected) 1f else 0.5f,
                        label = "MinuteAlpha"
                    )
                    Box(modifier = Modifier.height(cellHeight), contentAlignment = Alignment.Center){
                        Text(
                            text = minute.toString().padStart(2, '0'),
                            modifier = Modifier
                                .width(60.dp),
                            fontSize = with(LocalDensity.current) { animatedSize.toSp() },
                            color = Color.Black.copy(alpha = animatedAlpha),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}





@Preview(showBackground = true)
@Composable
fun PreviewTimePickerWheel() {
    SelfCareTheme {
        TimePickerWheel(selectedHour = 0, selectedMinute = 0, onHourChange = {}, onMinuteChange = {})
    }
}