package com.example.selfcare

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.selfcare.ui.theme.SelfCareTheme

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

@Preview(showBackground = true)
@Composable
fun PreviewNavBar() {
    SelfCareTheme {
        BottomNavBar()
    }
}