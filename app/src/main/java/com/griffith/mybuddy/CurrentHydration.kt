package com.griffith.mybuddy

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.content.res.Configuration
import androidx.compose.ui.graphics.SweepGradient
import android.health.connect.datatypes.ExerciseRoute
import android.location.Location
import android.location.Location.distanceBetween
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.URL
import java.util.Calendar
import java.util.concurrent.Executors

/**
 * Julia Dobrovodska
 * 3061278
 * https://github.com/DziJulia/Android-Project-3061278
 */
class CurrentHydration : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherRequest()

            //Reset hydration leve back to 0 every midnight
            LaunchedEffect(key1 = hydrationLevel) {
                while (true) {
                    delay(getTimeUntilMidnight())
                    hydrationLevel = 0
                }
            }

            Box(modifier = Modifier.fillMaxSize().then(activityBackground)) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = if (isLandscape()) Alignment.Start else Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.size(20.dp))
                    Text(
                        "Current Hydration",
                        modifier = Modifier.align(Alignment.Start).padding(start = 10.dp),
                        style = TextStyle(fontSize = 30.sp, fontWeight = FontWeight.Bold)
                    )
                    if (isLandscape()) {
                        Row() {
                            Column(
                                modifier = Modifier.weight(1f).offset(x = 80.dp, y = 20.dp),
                                horizontalAlignment = Alignment.Start
                            ) {
                                WaterButtons()
                            }
                            Box(
                                modifier = Modifier.weight(1f).offset(x = -70.dp, y = -25.dp)
                            ) {
                                HydrationCircle()
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize().padding(bottom = 100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                HydrationCircle()
                                Spacer(modifier = Modifier.size(20.dp))
                                WaterButtons()
                            }
                        }
                    }
                }
                MyButtonsRow()
                LogOutButton(modifier = Modifier.align(Alignment.TopEnd))
            }
        }

    }
}

/**
 * This is a Composable function that provides location updates.
 * @param onLocationChanged A callback function that is invoked with the new
 * location whenever the location changes.
 * This function requests location updates from the LocationManager and invokes the provided
 * callback function whenever the location changes. It automatically removes the
 * location updates when the composable is disposed.
 *
 * Note: This function requests the ACCESS_FINE_LOCATION permission if it is not already granted.
 * The calling code should handle the case where the user denies the permission request.
 */
@Composable
fun LocationUpdates(onLocationChanged: (Location) -> Unit) {
    val context = LocalContext.current
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?

    val locationListener = remember {
        object : LocationListener {
            override fun onLocationChanged(location: Location) {
                onLocationChanged(location)
            }

            @Deprecated("Deprecated in Java")
            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }
    }

    DisposableEffect(locationManager) {
        // Check for permissions
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(context as Activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                99)
        }

        locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, locationListener)

        onDispose {
            locationManager?.removeUpdates(locationListener)
        }
    }
}

/**
 * Calculates the time until midnight in milliseconds.
 * This function creates a Calendar instance representing the next midnight and subtracts the current time from it to get the time until midnight.
 * @return The time until midnight in milliseconds.
 */
fun getTimeUntilMidnight(): Long {
    val calendar = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val currentTime = Calendar.getInstance().timeInMillis
    return calendar.timeInMillis - currentTime
}

/**
 * This function is responsible for fetching weather data and sending notifications to the user.
 * It fetches the weather data for the user's current location every 30 minutes.
 * If the temperature exceeds 25 degrees Celsius, it sends a notification to the user.
 * The function also ensures that notifications are not sent more frequently than every 90 minutes.
 */
@Composable
fun WeatherRequest() {
    val API_KEY = "5e99e2e828c2a3d4b57fab4f8772528f"
    val context = LocalContext.current
    var weatherData by remember { mutableStateOf<JSONObject?>(null) }
    var lastNotificationTime by remember { mutableStateOf<Long?>(0) }
    //Variables for testing purposes to get notification
    // var mexicoTestlong = "-86.84656"
    // var mexicoTestlat = "21.17429"

    // Don't SPam the user send it only every 90 minutes once we know it is too hot
    // we do not need to do any extra requests.
    if (System.currentTimeMillis() - lastNotificationTime!! >= 90 * 60 * 1000L) {
        LocationUpdates { location ->
            //Test check if im getting correct location
            /**
             * val geocodingUrl = "https://api.opencagedata.com/geocode/v1/json?q=${location.latitude}+${location.longitude}&key=64d3b25aa4da48c7a43665b24067b2e7"
             * CoroutineScope(Dispatchers.IO).launch {
             *  val locationData = fetchWeatherData(geocodingUrl)
             *  val locationName = locationData.getJSONArray("results").getJSONObject(0).getJSONObject("components").getString("city")
             *  Log.d("LocationName", "Location name: $locationName")
             * }
            */
            val url =
                "https://api.openweathermap.org/data/2.5/weather?lat=${location.latitude}&lon=${location.longitude}&appid=$API_KEY"
            CoroutineScope(Dispatchers.IO).launch {
                weatherData = fetchWeatherData(url)
            }
        }

        LaunchedEffect(Unit) {
            while (true) {
                delay(30 * 60 * 1000L)
                //
                //delay(60 * 1000L)
            }
        }

        weatherData?.let {
            val temperature = it.getJSONObject("main").getDouble("temp") - 273.15
            if (temperature > 25) {
                sendNotification(context)
                lastNotificationTime = System.currentTimeMillis()
            }
        }
    }
}

/**
 * This is a suspending function that fetches weather data from a given URL.
 * @param url The URL from which the weather data is to be fetched.
 * It should be a valid URL string.
 * @return A JSONObject containing the weather data fetched from the URL, or
 * null if an error occurred. The structure of the JSONObject depends on the
 * API used.
 */
suspend fun fetchWeatherData(url: String): JSONObject? {
    return try {
        val deferred = CoroutineScope(Dispatchers.IO).async {
            val result = URL(url).readText()
            JSONObject(result)
        }
        deferred.await()
    } catch (e: Exception) {
        // Log the exception
        Log.d("error", "Error fetching data: ${e.message}")
        null
    }
}

/**
 * Sends a hydration reminder notification to the user.
 * @param context The application context. This is used to access system services and resources.
 *
 * This function creates a notification channel with high importance, then builds and sends a notification
 * through this channel. The notification has a title of "Hydration Reminder" and a message reminding the user
 * to drink water when the temperature is high. The notification icon is specified by `R.drawable.notification_icon`.
 *
 * Note: This function requires the `Manifest.permission.ACCESS_NOTIFICATION_POLICY` permission.
 */
fun sendNotification(context: Context) {
    val channelId = "HydrationChannel"
    val notificationChannel = NotificationChannel(
        channelId,
        "Hydration Notifications",
        NotificationManager.IMPORTANCE_HIGH
    )
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(notificationChannel)

    val notification = NotificationCompat.Builder(context, channelId)
        .setContentTitle("Hydration Reminder")
        .setContentText("The temperature is high. Don't forget to drink water!")
        .setSmallIcon(R.drawable.drop)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .build()

    Log.d("Notification", "NOTIFICATION BEEING SET: $notification")
    NotificationManagerCompat.from(context).notify(0, notification)
}

/**
 * This function returns the size of a button based on the orientation of the device.
 * @return Pair<Dp, Dp> - The width and height of the button in dp.
 * If the device is in landscape mode, it returns 150.dp for width and 35.dp for height.
 * If the device is not in landscape mode, it returns 150.dp for width and 60.dp for height.
 */
@Composable
fun buttonSize(): Pair<Dp, Dp> {
    return if (isLandscape()) Pair(150.dp, 35.dp) else Pair(150.dp, 60.dp)
}

/**
 * This function returns the size of a circle based on the orientation of the device.
 * @return Pair<Dp, Dp> - The width and height of the circle in dp.
 * If the device is in landscape mode, it returns 350.dp for width and 180.dp for height.
 * If the device is not in landscape mode, it returns 350.dp for width and 200.dp for height.
 */
@Composable
fun circleSize(): Pair<Dp, Dp> {
    return if (isLandscape()) Pair(350.dp, 180.dp) else Pair(350.dp, 200.dp)
}

/**
 * A composable function that creates a custom button with a specific look and feel.
 * @param text The text displayed on the button.
 * @param onClick The function to be executed when the button is clicked.
 */
@Composable
fun WaterButton(text: String, onClick: () -> Unit) {
    val (width, height) = buttonSize()

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(Color(192, 219, 236)),
        border = BorderStroke(1.dp, Color.Blue),
        shape = RectangleShape,
        modifier = Modifier
            .padding(top = 10.dp)
            .size(width, height)
    ) {
        Text(text, color = Color.Black)
    }
}

/**
 * This is a composable function that displays a dialog for the user to input a custom amount.
 * The dialog contains a text field where the user can input a number, and two buttons for confirmation and dismissal.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomAmountDialog(context: Context) {
    var customAmount by remember { mutableStateOf("") }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Add Custom Amount") },
            text = {
                TextField(
                    value = customAmount,
                    onValueChange = { customAmount = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (customAmount.toIntOrNull() != null) {
                        hydrationLevel += customAmount.toInt()
                        showDialog = false
                    } else {
                        //Handle of non numeric input
                        Toast.makeText(context, "Please enter a valid number", Toast.LENGTH_SHORT).show()
                    } }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/**
 * A composable function that creates a set of custom buttons with specific labels.
 * The arrangement of the buttons changes based on the screen orientation.
 */
@Composable
fun WaterButtons() {
    val context = LocalContext.current
    val isLandscape = isLandscape()
    val buttonLabels = listOf("Water 250ml", "Water 300ml", "Water 500ml", "Add ml")
    val spacerModifier = if (isLandscape) Modifier else Modifier.size(20.dp)

    Spacer(modifier = spacerModifier)

    CustomAmountDialog(context)

    Column(verticalArrangement = Arrangement.SpaceEvenly) {
        if (isLandscape) {
            buttonLabels.forEach { label ->
                WaterButton(label) { }
                Spacer(modifier = Modifier.size(5.dp))
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column {
                    WaterButton(buttonLabels[0]) { hydrationLevel += 250 }
                    WaterButton(buttonLabels[1]) { hydrationLevel += 300 }
                }
                Column {
                    WaterButton(buttonLabels[2]) { hydrationLevel += 500 }
                    WaterButton(buttonLabels[3]) { showDialog = true }
                }
            }
        }
    }
}

/**
 * `hydrationLevel` is a mutable state that holds the current hydration level.
 * It's an integer that increases based on the amount of water intake.
 * This state is observed by Jetpack Compose and any changes to this state
 * will recompose all composables that read this state.
 */
var hydrationLevel by mutableStateOf(0)

/**
 * `showDialog` is a mutable state that controls the visibility of the dialog.
 * When `showDialog` is true, the dialog is visible. When `showDialog` is false,
 * the dialog is dismissed. This state is observed by Jetpack Compose and any changes
 * to this state will recompose all composables that read this state.
 */
var showDialog by mutableStateOf(false)


/**
 * Function that displays a hydration circle with colored circles and text.
 */
@Composable
fun HydrationCircle() {
    var hydrationGoal = 4000
    // Rounding the number 2 decimals, since .round didnt work using this approach
    val percentage = String.format("%.2f", (hydrationLevel.toFloat() / hydrationGoal.toFloat()) * 100).toFloat()
    val borderColor = Color.Black
    val progressColor = Color.Blue

    val (blueSize, whiteSize) = circleSize()
    Box(modifier = Modifier.size(blueSize), contentAlignment = Alignment.Center) {
        // Bigger Blue Circle
        // This creates a Canvas composable that matches the size of its parent.
        Canvas(modifier = Modifier.matchParentSize()) {
            // This draws an arc (a portion of a circle's circumference) within the Canvas.
            // The arc represents the progress towards the hydration goal.
            // The color of the arc is determined by the variable 'progressColor'.
            // The arc starts at -90 degrees (the top of the circle) and its length
            // (sweepAngle) is calculated based on the percentage of the hydration goal achieved.
            // The 'useCenter' parameter is set to true, which means the arc will
            // be a sector (a part of the circle enclosed by two radii and an arc).
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = 360f * (percentage / 100f),
                useCenter = true
            )
            // This draws a full circle on the border of the Canvas.
            // The color of the circle is determined by the variable 'borderColor'.
            // The 'style' parameter is set to Stroke, which means only the outline
            // of the circle will be drawn.
            // The width of the circle's outline is determined by '2.dp.toPx()'.
            drawCircle(color = borderColor, style = Stroke(width = 2.dp.toPx()))
        }

        // Smaller White Circle
        Box(modifier = Modifier.size(whiteSize), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.matchParentSize()) {
                drawCircle(color = Color.White)
                drawCircle(color = borderColor, style = Stroke(width = 2.dp.toPx()))
            }

            // Text inside the smaller circle
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "$percentage %", style = TextStyle(fontSize = 20.sp))
                Text(text = "$hydrationLevel ml", style = TextStyle(fontSize = 16.sp))
                Text(text = "Goal: $hydrationGoal ml", style = TextStyle(fontSize = 16.sp))
            }
        }
    }
}

