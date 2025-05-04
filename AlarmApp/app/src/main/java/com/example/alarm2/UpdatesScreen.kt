package com.example.alarm2

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.alarm2.data.ForecastResponse
import com.example.alarm2.data.WeatherResponse
import com.example.alarmapp.MainWeatherBlock
import com.example.alarmapp.grayColor
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UpdatesScreen() {
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
                    icon = weather.weather[0].icon
                )
            }
        }
        DailyTempBlock()
        DetailsBlock()
    }

}



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainWeatherBlock(
    location: String,
    temperature: String,
    description: String,
    icon: String
) {
    val imageUrl = "https://openweathermap.org/img/wn/${icon}@2x.png"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp, start = 5.dp, end = 5.dp, bottom= 3.dp)
            .background(grayColor, shape = RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = temperature,
                style = TextStyle(fontSize = 90.sp, fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.width(50.dp))

            // TODO: Implement Image loading from URL (using Coil, Glide, or rememberAsyncImagePainter)
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
                text = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy. MM. d.")), // Dynamic date
                style = TextStyle(fontSize = 25.sp, fontWeight = FontWeight.Medium)
            )
        }
    }
}

@Composable
fun DailyTempBlock() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 3.dp, vertical = 3.dp)
            .background(grayColor, shape = RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DailyTemp("H", "13°")
            DailyTemp("K", "17°")
            DailyTemp("Sze", "25°")
            DailyTemp("Cs", "15°")
            DailyTemp("P", "8°")
            DailyTemp("Szo", "13°")
            DailyTemp("V", "9°")
        }
    }
}

@Composable
fun DailyTemp(day: String, temp: String) {
    var blue = Color(0xFF2698CC)
    var isClicked by remember { mutableStateOf(false) }
    val textColor = if (isClicked) blue else Color.Black
    Column(horizontalAlignment = Alignment.CenterHorizontally) { // Center items horizontally
        Text(
            text = day,
            color = textColor,
            style = TextStyle(fontSize = 30.sp, fontWeight = FontWeight.Medium),
            modifier = Modifier.clickable { isClicked = !isClicked }
        )
        Text(
            text = temp,
            color = textColor,
            style = TextStyle(fontSize = 28.sp),
            modifier = Modifier.clickable { isClicked = !isClicked }
        )
    }
}

@Composable
fun DetailsBlock() {
    val updateItems = listOf(
        "Ruházati\najánló",
        "Közlekedési\njavaslat",
        "Reggeli ital\najánló",
        "Napi aktivitás\njavaslat"
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxWidth()
            .height(600.dp)
            .padding(start = 3.dp, end = 3.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(updateItems.size) { index -> // This overload correctly handles a List<String>
            DetailItem(text = updateItems[index])
        }
    }
}

@Composable
fun DetailItem(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(grayColor, shape = RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Medium),
            modifier = Modifier.padding(5.dp)
        )
    }
}
