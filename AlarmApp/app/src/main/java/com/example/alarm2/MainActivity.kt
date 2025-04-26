package com.example.alarm2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.example.alarmapp.HomeScreen
import com.example.alarmapp.Navigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
            }
        }
    }


@Composable
fun MyApp() {
    // The navigation bar and content are handled in the Navigation composable
    Navigation()
}