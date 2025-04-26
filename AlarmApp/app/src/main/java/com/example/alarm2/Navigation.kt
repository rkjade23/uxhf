package com.example.alarmapp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.unit.dp
import com.example.alarm2.NewAlarmScreen
import com.example.alarm2.ToDoListScreen
import com.example.alarm2.UpdatesScreen
import com.example.alarmapp.HomeScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigation() {
    // State for tracking the selected tab (screen)
    val selectedTab = remember { mutableStateOf(0) }

    // Scaffold with bottom navigation
    Scaffold(
        bottomBar = {
            BottomNavigationBar(selectedTab = selectedTab.value) { selectedTab.value = it }
        }
    ) { padding ->
        // Content area where different screens are shown
        Column(modifier = Modifier.fillMaxSize()) {
            when (selectedTab.value) {
                0 -> HomeScreen(onClickMainWeatherBlock = {
                    selectedTab.value = 2 // Navigate to Favorites
                }, onClickNewAlarmButton = {
                    selectedTab.value = 3 // Navigate to New Alarm Screen
                })
                1 -> ToDoListScreen() // To-Do List Screen
                2 -> UpdatesScreen() // Updates Screen
                3 -> NewAlarmScreen(onClickSaveButton = {
                    selectedTab.value = 0 // Navigate to Favorites
                }) // New Alarm Screen
            }
        }
    }
}

@Composable
fun BottomNavigationBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    BottomNavigation( // Adjust the height of the navigation bar here
        backgroundColor = Color(0xFFF4F4F4),
        contentColor = Color(0xFF3D3D3D)
    ) {
        BottomNavigationItem(
            modifier = Modifier.padding(bottom = 5.dp),
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = {
                Text(
                    "Home",
                    modifier = Modifier.padding(bottom = 8.dp) // Add bottom padding
                )
            }
        )
        BottomNavigationItem(
            modifier = Modifier.padding(bottom = 5.dp),
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            icon = { Icon(Icons.Filled.List, contentDescription = "To-do List") },
            label = {
                Text(
                    "To-do List",
                    modifier = Modifier.padding(bottom = 8.dp) // Add bottom padding
                )
            }
        )
        BottomNavigationItem(
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) },
            icon = { Icon(Icons.Filled.Favorite, contentDescription = "Updates") },
            label = {
                Text(
                    "Updates",
                    modifier = Modifier.padding(bottom = 8.dp) // Add bottom padding
                )
            }
        )
    }
}

