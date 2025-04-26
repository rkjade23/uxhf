// HomePage.kt
package com.example.alarmapp

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.alarm2.NewAlarmScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import com.example.alarm2.ToDoListScreen
import com.example.alarm2.UpdatesScreen
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    onClickMainWeatherBlock: () -> Unit,
    onClickNewAlarmButton: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp)

    ) {
        // Main Weather Block (clickable to navigate to Favorites)
        MainWeatherBlock(onClick = onClickMainWeatherBlock)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            WeatherDetailsBlock()
            HourlyTemperatureBlock()
        }

        // New Alarm Button (clickable to navigate to New Alarm Screen)
        NewAlarmButton(onClick = onClickNewAlarmButton)

        AlarmList()
    }
}

val grayColor = Color(0xFFD9D9D9)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainWeatherBlock(onClick: () -> Unit) {
    val currentDate = LocalDate.now()
    val dayOfWeek = currentDate.dayOfWeek.getDisplayName(java.time.format.TextStyle.FULL, Locale("hu", "HU")).capitalize()
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy. M. d.")
    val formattedDate = currentDate.format(dateFormatter)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp, start = 5.dp, end = 5.dp)
            .background(grayColor, shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically // Align vertically
        ) {
            // Temperature (large and bold)
            Text(
                text = "13°",
                style = TextStyle(fontSize = 100.sp, fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.width(50.dp)) // Space between the temperature and the icon

            // Weather Icon (cloud with sun)
            Icon(
                imageVector = Icons.Filled.Favorite, // You can swap this icon to match your design
                contentDescription = "Weather Icon",
                modifier = Modifier.size(120.dp), // Adjust the size of the icon
                tint = Color(0xFFFBBC04) // Icon color, like the yellow from the image
            )
        }

        Spacer(modifier = Modifier.height(20.dp)) // Space between the temperature and location/date

        // Location and Date below the temperature
        Row(
            horizontalArrangement = Arrangement.Center, // Center the content
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Location (smaller font size)
            Text(
                text = "Budapest",
                style = TextStyle(fontSize = 30.sp, fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.width(50.dp))

            // Date (smaller font size)
            Text(
                text = formattedDate,
                style = TextStyle(fontSize = 27.sp, fontWeight = FontWeight.Medium)
            )
        }
        }
}


@Composable
fun WeatherDetailsBlock() {
    Column(
        modifier = Modifier
            .fillMaxHeight()     // Ensures the block takes up 50% of the space in the Row
            .padding(horizontal = 3.dp, vertical= 5.dp )       // Padding around the whole block
            .background(grayColor, shape = RoundedCornerShape(16.dp)), // Gray background with rounded corners
        horizontalAlignment = Alignment.Start
    ) {
        // Weather Icons and Text, with consistent padding and font sizes
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp) // Padding inside the row
        ) {
            Icon(
                imageVector = Icons.Default.Favorite, // Humidity Icon
                contentDescription = "Humidity",
                modifier = Modifier.size(24.dp),
                tint = Color(0xFF1E88E5) // Color for the humidity icon
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Páratartalom: 32%", style = TextStyle(fontSize = 20.sp))
        }

        Spacer(modifier = Modifier.height(5.dp)) // Vertical space between rows

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp) // Padding inside the row
        ) {
            Icon(
                imageVector = Icons.Default.Favorite, // Wind Icon
                contentDescription = "Wind",
                modifier = Modifier.size(24.dp),
                tint = Color(0xFF90A4AE) // Color for the wind icon
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Szél: 11 km/h", style = TextStyle(fontSize = 20.sp))
        }

        Spacer(modifier = Modifier.height(5.dp)) // Vertical space between rows

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp) // Padding inside the row
        ) {
            Icon(
                imageVector = Icons.Default.Favorite, // Pollen Icon
                contentDescription = "Pollen",
                modifier = Modifier.size(24.dp),
                tint = Color(0xFF388E3C) // Color for the pollen icon
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Pollen: Magas", style = TextStyle(fontSize = 20.sp))
        }
    }
}


data class HourlyData(val hour: String, val temperature: String)

@Composable
fun HourlyTemperatureBlock() {

    val hourlyData = listOf(
        HourlyData("8:00", "6°"),
        HourlyData("9:00", "8°"),
        HourlyData("10:00", "11°"),
        HourlyData("11:00", "12°")
    )
    Column(
        modifier = Modifier
            .fillMaxHeight()   // Ensures the block takes up 50% of the space in the Row
            .padding(horizontal = 2.dp, vertical= 5.dp )
            .background(grayColor, shape = RoundedCornerShape(16.dp)),
        horizontalAlignment = Alignment.Start
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxHeight()
        ) {
            items(8) { index -> // Replace '4' with dynamic data size if needed
                // Replace each item with actual data, here it's static
                HourlyTemperature(hour = "8:00", temperature = "6°")
                Spacer(modifier = Modifier.height(5.dp)) // Space between items
            }
        }
    }
}

@Composable
fun HourlyTemperature(hour: String, temperature: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = hour, style = TextStyle(fontSize = 20.sp))
        Text(text = temperature, style = TextStyle(fontSize = 20.sp))
    }
}

@Composable
fun NewAlarmButton(onClick: () -> Unit) {
    Button(
        onClick = onClick, // Navigate to the new alarm screen when clicked
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 5.dp, bottom= 3.dp, end= 5.dp),
                shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = grayColor) // Purple button color
    ) {
        Icon(
            imageVector = Icons.Default.Add, // '+' icon
            contentDescription = "New Alarm",
            modifier = Modifier
                .size(35.dp) // Icon size
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = "Új ébresztő", style = TextStyle(fontSize = 30.sp, color = Color.Black))
    }
}

@Composable
fun AlarmList (){
    LazyColumn(
        modifier = Modifier.fillMaxHeight().padding(top= 5.dp, bottom = 45.dp)
    ) {
        items(15) { index ->
            AlarmItem(hour = "7:30", isEnabled = true)
        }
    }
}
@Composable
fun AlarmItem(hour: String, isEnabled: Boolean) {
    // Use remember and mutableStateOf to make the toggle reactive
    val toggleState = remember { mutableStateOf(isEnabled) } // Initialize the state for the switch

    // Wrapping each item in a Box to give it a gray background
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 3.dp, vertical = 3.dp)
            .background(grayColor, shape = RoundedCornerShape(16.dp)) // Gray background with rounded corners
            .padding(12.dp) // Padding inside the item
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = hour, style = TextStyle(fontSize = 30.sp))
            // Bind the Switch state to the toggleState variable
            Switch(
                checked = toggleState.value, // Access the value of the state
                onCheckedChange = { toggleState.value = it } // Update the state when the switch is toggled
            )
        }
    }
}

