package com.example.alarm2

import android.os.Build
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import com.example.alarm2.ToDoListScreen
import com.example.alarm2.UpdatesScreen
import com.example.alarmapp.HourlyTemperatureBlock
import com.example.alarmapp.WeatherDetailsBlock
import com.example.alarmapp.grayColor
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import com.example.alarmapp.NewAlarmButton
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NewAlarmScreen(
    onClickSaveButton: () -> Unit
) {
    val lazyRowState = rememberLazyListState() // Létrehozzuk a LazyRow állapotát
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(3.dp) // Padding for overall screen spacing
    ) {
        // Time Display
        TimeDisplay()

        // H, K, Sze, Cs, P, Szo, V text buttons
        TextButtons()
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // First row of toggle settings (Hang, Rezgés)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ToggleSetting(
                    settingName = "Hang",
                    iconResId = R.drawable.ic_music_player // Reference your drawable resource here
                )
                ToggleSetting(
                    settingName = "Rezgés",
                    iconResId = R.drawable.ic_buzz // Reference your drawable resource here
                )
            }
            Spacer(modifier = Modifier.height(2.dp)) // Add some space between rows

            // Second row of toggle settings (Szundi, Reggeli rutin)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ToggleSetting(
                    settingName = "Szundi",
                    iconResId = R.drawable.ic_snooze // Reference your drawable resource here
                )
                ToggleSetting(
                    settingName = "Reggeli Rutin",
                    iconResId = R.drawable.ic_checked // Reference your drawable resource here
                )
            }
        }
        SaveButton(onClick = onClickSaveButton)
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            state = lazyRowState, // Átadjuk az állapotot a LazyRow-nak
            flingBehavior = rememberSnapFlingBehavior(lazyListState = lazyRowState), // Átadjuk az állapotot a flingBehavior-nak
            contentPadding = PaddingValues(horizontal = 0.dp) // Nincs extra padding a széleken
        ) {
            item {
                CalendarBox(modifier = Modifier.fillParentMaxWidth(), currentDate = LocalDate.now().plusDays(1))
            }
            item {
                CalendarBox(modifier = Modifier.fillParentMaxWidth(), currentDate = LocalDate.now().plusDays(2))
            }
            // További napok hozzáadása itt
        }
    }
}

val grayColor = Color(0xFFD9D9D9)

@Composable
fun TimeDisplay() {
    var selectedHour by remember { mutableStateOf(7) }
    var selectedMinute by remember { mutableStateOf(30) }
    var showTimePickerDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp, start = 5.dp, end = 5.dp)
            .background(Color(0xFFD9D9D9), shape = RoundedCornerShape(16.dp))
            .padding(12.dp)
            .clickable { showTimePickerDialog = true } // Show dialog on click
    ) {
        Text(
            text = String.format("%02d:%02d", selectedHour, selectedMinute),
            style = TextStyle(fontSize = 70.sp, fontWeight = FontWeight.Medium),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }

    if (showTimePickerDialog) {
        AlertDialog(
            onDismissRequest = { showTimePickerDialog = false },
            title = { Text("Állíts be időt") },
            text = {
                AndroidView(
                    factory = { context ->
                        TimePicker(context).apply {
                            hour = selectedHour
                            minute = selectedMinute
                            setOnTimeChangedListener { _, hourOfDay, minute ->
                                selectedHour = hourOfDay
                                selectedMinute = minute
                            }
                        }
                    },
                    update = { view ->
                        // Update the TimePicker if selectedHour or selectedMinute changes
                        view.hour = selectedHour
                        view.minute = selectedMinute
                    }
                )
            },
            confirmButton = {
                TextButton(onClick = { showTimePickerDialog = false }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePickerDialog = false }) {
                    Text("Mégse")
                }
            }
        )
    }
}

@Composable
fun TextButtons() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 3.dp, vertical = 3.dp)
            .background(grayColor, shape = RoundedCornerShape(16.dp)) // Gray background with rounded corners
            .padding(12.dp) // Padding inside the item
    ){
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween

        ) {
            TextButton("H")
            TextButton("K")
            TextButton("Sze")
            TextButton("Cs")
            TextButton("P")
            TextButton("Szo")
            TextButton("V")
        }
    }
}

@Composable
fun TextButton(label: String) {
    var blue = Color(0xFF2698CC)
    var isClicked by remember { mutableStateOf(false) }
    val textColor = if (isClicked) blue else Color.Black

    Text(
        text = label,
        color = textColor,
        style = TextStyle(fontSize = 35.sp, fontWeight = FontWeight.Medium),
        modifier = Modifier.clickable { isClicked = !isClicked }
    )
}



@Composable
fun ToggleSetting(settingName: String,iconResId: Int) {
    val toggleState = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .width(197.dp) // Set a fixed width for each toggle setting
            .padding(vertical = 1.dp) // Padding for each setting block
            .background(grayColor, shape = RoundedCornerShape(16.dp)) // Background with rounded corners
            .padding(12.dp) // Padding inside the block
    ) {
        Column() {
            Row(
                modifier = Modifier.fillMaxWidth(), // This makes the row take up the full width
                horizontalArrangement = Arrangement.Center, // This centers the items horizontally in the Row
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = settingName,
                    style = TextStyle(fontSize = 25.sp, fontWeight = FontWeight.Medium),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Icon on the left
                Icon(
                    painter = painterResource(id = iconResId),
                    contentDescription = settingName,
                    modifier = Modifier.size(65.dp),
                    tint = Color.Black
                )

                // Text


                // Switch on the right
                Switch(
                    checked = toggleState.value,
                    onCheckedChange = { toggleState.value = it }
                )
            }
        }
    }
}

@Composable
fun SaveButton(onClick: () -> Unit) {
    Button(
        onClick = onClick, // Navigate to the new alarm screen when clicked
        modifier = Modifier
            .fillMaxWidth()
            .padding(top= 5.dp, start = 5.dp, bottom= 3.dp, end= 5.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFBDBDBD)) // Purple button color
    ) {
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = "Mentés", style = TextStyle(fontSize = 30.sp, color = Color.Black, fontWeight = FontWeight.Bold))
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarBox(modifier: Modifier = Modifier, currentDate: LocalDate ){
    Box(
        modifier = modifier
            .padding(horizontal = 3.dp, vertical = 3.dp)
            .background(grayColor) // Gray background with rounded corners
            .padding(bottom= 45.dp)
    ){
        Column(
            modifier = Modifier.padding(3.dp)
        ) {
            DateHeader(currentDate)
            ScheduleList()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateHeader(currentDate: LocalDate) {
    val dayOfWeek = currentDate.dayOfWeek.getDisplayName(java.time.format.TextStyle.FULL, Locale("hu", "HU")).capitalize()
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy. M. d.")
    val formattedDate = currentDate.format(dateFormatter)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp)
            .background(Color.White) // Gray background with rounded corners
            .padding(12.dp) // Padding inside the item
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White) // Fehér sáv
                .padding(vertical = 8.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = dayOfWeek,
                style = TextStyle(fontSize = 30.sp, fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.width(30.dp))
            Text(
                text = formattedDate,
                style = TextStyle(fontSize = 25.sp)
            )
        }
    }
}

@Composable
fun ScheduleList() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp)
    ) {
        LazyColumn (
        ) {
            item {
                ScheduleItem(hour = "8:00", event = "Monday Wake-Up Hour", color = Color(0xFFDDE6ED))
            }
            item {
                ScheduleItem(hour = "9:00", event = "All-Team Kickoff", color = Color(0xFFDDE6ED))
            }
            item {
                ScheduleItem(hour = "10:00", event = "Financial Update", color = Color(0xFFDDE6ED))
            }
            item {
                ScheduleItem(
                    hour = "11:00",
                    event = "New Employee Welcome Lunch!",
                    color = Color(0xFFE9D5FF)
                )
            }
            item {
                ScheduleItem(hour = "12:00", event = "Team meeting", color = Color(0xFFDDE6ED))
            }
            item {
                ScheduleItem(hour = "13:00", event = "Meeting w/ Chloe", color = Color(0xFFDDE6ED))
            }
            item {
                ScheduleItem(hour = "14:00", event = "Yoga class", color = Color(0xFFE9D5FF))
            }
        }
    }
}

@Composable
fun ScheduleItem(hour: String, event: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(color = color)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = hour,
            style = TextStyle(fontSize = 20.sp),
            modifier = Modifier.width(60.dp) // Fix width for the hour
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = event,
            style = TextStyle(fontSize = 25.sp),
            modifier = Modifier.weight(1f)
        )
    }
}