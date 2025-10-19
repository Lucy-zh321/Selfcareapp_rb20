package com.example.selfcare


import android.util.Log
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.compose.ui.graphics.Color
fun calculateEndTime(startTime: String, lengthLabel: String): String {
    return try {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val start = sdf.parse(startTime)
        val calendar = Calendar.getInstance().apply { time = start!! }

        val minutesToAdd = when (lengthLabel) {
            "1" -> 1
            "15" -> 15
            "30" -> 30
            "45" -> 45
            "1h" -> 60
            "1.5h" -> 90
            else -> 0
        }

        // If length is 1 minute (or 0), just return start time without range
        if (minutesToAdd <= 1) {
            startTime
        } else {
            calendar.add(Calendar.MINUTE, minutesToAdd)
            val endTime = sdf.format(calendar.time)
            "$startTime - $endTime"
        }
    } catch (e: Exception) {
        startTime
    }

}

fun calculateDurationFromRange(range: String): Int? {
    return try {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val parts = range.split(" - ")
        if (parts.size == 2) {
            val start = sdf.parse(parts[0])?.time ?: return null
            val end = sdf.parse(parts[1])?.time ?: return null
            ((end - start) / (1000 * 60)).toInt()
        } else null
    } catch (e: Exception) {
        null
    }
}

// Helper function to calculate duration from time range
fun calculateDurationFromTimes(startTime: String, endTime: String): String? {
    return try {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val start = sdf.parse(startTime)?.time ?: return null
        val end = sdf.parse(endTime)?.time ?: return null
        val minutes = ((end - start) / (1000 * 60)).toInt()

        when (minutes) {
            1 -> "1"
            15 -> "15"
            30 -> "30"
            45 -> "45"
            60 -> "1h"
            90 -> "1.5h"
            else -> null
        }
    } catch (e: Exception) {
        null
    }
}


fun generateTimeOptions(): List<String> {
    val times = mutableListOf<String>()
    for (hour in 0..23) {
        for (minute in listOf(0, 15, 30, 45)) {
            times.add(String.format(Locale.getDefault(),"%02d:%02d", hour, minute))
        }
    }
    return times
}

fun calculateDurationInMinutes(startHour: Int, startMinute: Int, endHour: Int, endMinute: Int): Int {
    val start = startHour * 60 + startMinute
    val end = endHour * 60 + endMinute
    return if (end >= start) {
        end - start
    } else {
        24 * 60 - (start - end) // wrap around midnight
    }
}

fun isCustomDuration(duration: String?): Boolean {
    return when {
        duration == null -> false
        duration.endsWith("m") -> {
            val minutes = duration.removeSuffix("m").toIntOrNull() ?: return false
            minutes !in listOf(1, 15, 30, 45)
        }
        else -> false
    }
}


// Add this to your utility functions
fun roundToNearestStandard(minute: Int): Int {
    return when {
        minute < 7 -> 0
        minute < 22 -> 15
        minute < 37 -> 30
        minute < 52 -> 45
        else -> 0 // Next hour
    }
}fun clampEndTime(hour: Int, minute: Int): Pair<Int, Int> {
    return when {
        hour >= 24 -> 23 to 59  // Never allow times past 23:59
        minute >= 60 -> hour to 59
        else -> hour to minute
    }
}



// Add this at the top level (outside any function)
fun getSelectedColor(selectedColor: Color?): Color {
    return selectedColor ?: Color(0xFFFFB6C1) // Fallback to original pink if no color selected
}

