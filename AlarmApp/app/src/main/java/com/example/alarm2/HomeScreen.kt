// HomePage.kt
package com.example.alarmapp

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.alarm2.RetrofitClient
import com.example.alarm2.data.ForecastItem
import com.example.alarm2.data.ForecastResponse
import com.example.alarm2.data.WeatherResponse
import com.example.alarm2.NewAlarmScreen
import com.example.alarm2.ToDoListScreen
import com.example.alarm2.UpdatesScreen
import com.example.alarm2.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    onClickMainWeatherBlock: () -> Unit,
    onClickNewAlarmButton: () -> Unit
) {
    val apiKey = "a771bdf215c8bc458b37e13321457292"
    val city = "Budapest"
    val weatherData = remember { mutableStateOf<WeatherResponse?>(null) }
    val forecastData = remember { mutableStateOf<ForecastResponse?>(null) }
    val loading = remember { mutableStateOf(true) }
    val detailsLazyRowState = rememberLazyListState()
    val detailsBlockWidth = remember { mutableStateOf(200.dp) }

    LaunchedEffect(key1 = Unit) {
        try {
            val weatherResponse = RetrofitClient.instance.getWeather(city, apiKey)
            if (weatherResponse.isSuccessful) {
                weatherData.value = weatherResponse.body()
                println("Current weather loaded successfully: ${weatherData.value}")
            } else {
                println("Error fetching current weather: ${weatherResponse.code()} ${weatherResponse.message()}")
                println("Error body: ${weatherResponse.errorBody()?.string()}")
            }

            val forecastResponse = RetrofitClient.instance.getForecast(city, apiKey)
            if (forecastResponse.isSuccessful) {
                forecastData.value = forecastResponse.body()
            } else {
                println("Error fetching forecast: ${forecastResponse.code()} ${forecastResponse.message()}")
                println("Error body (forecast): ${forecastResponse.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            println("Network error: ${e.localizedMessage}")
        } finally {
            loading.value = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp)
    ) {
        if (loading.value) {
            Text("Loading weather...")
        } else {
            weatherData.value?.let { weather ->
                MainWeatherBlock(
                    location = "${weather.name}",
                    temperature = "${(weather.main.temp - 273.15).toInt()}°C",
                    description = weather.weather[0].description,
                    icon = weather.weather[0].icon,
                    onClick = onClickNewAlarmButton
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(detailsBlockWidth.value)
                            .fillMaxHeight()
                    ) {
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            state = detailsLazyRowState,
                            flingBehavior = rememberSnapFlingBehavior(lazyListState = detailsLazyRowState),
                        ) {
                            item(key = "currentWeather") {
                                WeatherDetailsBlock(
                                    weather = weather,
                                    forecastItem = null,
                                    modifier = Modifier.fillParentMaxHeight().fillMaxWidth()
                                )
                            }
                            forecastData.value?.list?.firstOrNull()?.let { firstForecastItem ->
                                item(key = "forecastWeather") {
                                    WeatherDetailsBlock(
                                        weather = null,
                                        forecastItem = firstForecastItem,
                                        modifier = Modifier.fillParentMaxHeight().fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                    forecastData.value?.let { forecast ->
                        val hourlyTemperatures = forecast.list
                            .groupBy { it.dt_txt.split(" ")[1].substringBeforeLast(":") }
                            .map { (hourMinute, items) ->
                                val firstItem = items.first()
                                HourlyData(hourMinute, "${(firstItem.main.temp - 273.15).toInt()}°")
                            }
                            .take(8)
                        HourlyTemperatureBlock(
                            hourlyData = hourlyTemperatures,
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        )
                    }
                }
            } ?: run { Text("Could not load weather data.") }
        }

        NewAlarmButton(onClick = onClickNewAlarmButton)
        AlarmList()
    }
}

val grayColor = Color(0xFFD9D9D9)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainWeatherBlock(
    location: String,
    temperature: String,
    description: String,
    icon: String,
    onClick: () -> Unit
) {
    val imageUrl = "https://openweathermap.org/img/wn/${icon}@2x.png"
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp, start = 5.dp, end = 5.dp, bottom = 3.dp)
            .background(grayColor, shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = temperature,
                style = TextStyle(fontSize = 90.sp, fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.width(50.dp))
            AsyncImage(
                model = imageUrl,
                contentDescription = description,
                modifier = Modifier.size(180.dp),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = location,
                style = TextStyle(fontSize = 30.sp, fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.width(50.dp))
            Text(
                text = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy. MM. dd.")),
                style = TextStyle(fontSize = 25.sp, fontWeight = FontWeight.Medium)
            )
        }
    }
}

@Composable
fun WeatherDetailsBlock(
    weather: WeatherResponse?,
    forecastItem: ForecastItem?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(horizontal = 3.dp, vertical = 5.dp)
            .width(198.dp)
            .background(grayColor, shape = RoundedCornerShape(16.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        val title = when {
            weather != null -> "Jelenlegi részletek"
            forecastItem != null -> "Következő részletek"
            else -> "Részletek"
        }
        Text(
            text = title,
            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (weather != null) {
            weather.main?.humidity?.let { humidity ->
                DetailRow(
                    icon = painterResource(id = R.drawable.ic_humidity),
                    label = "Páratartalom",
                    value = "${humidity}%"
                )
            }
            weather.wind?.let { wind ->
                DetailRow(
                    icon = painterResource(id = R.drawable.ic_wind),
                    label = "Szélerősség",
                    value = "${weather.main.humidity}%"
                )
            }
            weather.clouds?.let { clouds ->
                DetailRow(
                    icon = painterResource(id = R.drawable.ic_cloud),
                    label = "Felhőzet",
                    value = "${clouds.all}%"
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Későbbi adatokért lapozz",
                    style = TextStyle(fontSize = 15.sp, color = Color.Gray)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = "Lapozzon tovább",
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
            }
        } else if (forecastItem != null) {
            DetailRow(
                icon = painterResource(id = R.drawable.ic_humidity),
                label = "Páratartalom",
                value = "${forecastItem.main.humidity}%"
            )
            forecastItem.wind?.let { wind ->
                DetailRow(
                    icon = painterResource(id = R.drawable.ic_wind),
                    label = "Szél",
                    value = "${String.format("%.1f", wind.speed * 3.6)} km/h"
                )
            }
            forecastItem.clouds?.let { clouds ->
                DetailRow(
                    icon = painterResource(id = R.drawable.ic_cloud),
                    label = "Felhőzet",
                    value = "${clouds.all ?: "N/A"}%"
                )
            }
            Text(
                text = "Idő: ${forecastItem.dt_txt.split(" ")[1].substringBeforeLast(":")}",
                style = TextStyle(fontSize = 14.sp, color = Color.Gray),
                modifier = Modifier.padding(top = 8.dp)
            )
        } else {
            Text("Nincs adat")
        }
    }
}

@Composable
fun DetailRow(icon: Painter, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 6.dp)
    ) {
        Icon(
            painter = icon,
            contentDescription = label,
            modifier = Modifier.size(26.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$label: $value",
            style = TextStyle(fontSize = 18.sp)
        )
    }
}

data class HourlyData(val hour: String, val temperature: String)

@Composable
fun HourlyTemperatureBlock(
    hourlyData: List<HourlyData>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(top = 4.dp, bottom = 5.dp, end = 5.dp)
            .background(grayColor, shape = RoundedCornerShape(16.dp))
            .padding(5.dp),
        horizontalAlignment = Alignment.Start
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxHeight()
        ) {
            items(hourlyData.size) { index ->
                val data = hourlyData[index]
                HourlyTemperature(hour = data.hour, temperature = data.temperature)
                if (index < hourlyData.size - 1) {
                    Spacer(modifier = Modifier.height(5.dp))
                }
            }
        }
    }
}

@Composable
fun HourlyTemperature(hour: String, temperature: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = hour, style = TextStyle(fontSize = 20.sp))
        Text(text = temperature, style = TextStyle(fontSize = 20.sp))
    }
}

@Composable
fun NewAlarmButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 5.dp, bottom = 3.dp, end = 5.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = grayColor)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "New Alarm",
            modifier = Modifier.size(35.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = "Új ébresztő", style = TextStyle(fontSize = 30.sp, color = Color.Black))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AlarmList() {
    // Use a mutableStateList to track alarms so we can remove them
    val alarms = remember { mutableStateListOf<String>().apply { repeat(3) { add("7:30") } } }

    LazyColumn(
        modifier = Modifier
            .fillMaxHeight()
            .padding(top = 5.dp, bottom = 45.dp)
    ) {
        itemsIndexed(alarms) { index, hour ->
            AlarmItem(
                hour = hour,
                isEnabled = true,
                onDelete = { alarms.removeAt(index) }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AlarmItem(
    hour: String,
    isEnabled: Boolean,
    onDelete: () -> Unit
) {
    val toggleState = remember { mutableStateOf(isEnabled) }
    val showDialog = remember { mutableStateOf(false) }
    val onDismiss = { showDialog.value = false }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 3.dp, vertical = 3.dp)
            .background(grayColor, shape = RoundedCornerShape(16.dp))
            .combinedClickable(
                onClick = { /* no-op or could toggle enable state here */ },
                onLongClick = { showDialog.value = true }
            )
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = hour, style = TextStyle(fontSize = 30.sp))
            Switch(
                checked = toggleState.value,
                onCheckedChange = { toggleState.value = it }
            )
        }
    }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Feladat törlése") },
            text = { Text("Biztos törölni szeretnéd a feladatot?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF2698CC),
                        contentColor = Color.White
                    )
                ) {
                    Text("Törlés")
                }
            },
            dismissButton = {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF2698CC),
                        contentColor = Color.White
                    )
                ) {
                    Text("Mégse")
                }
            }
        )
    }
}
