package com.griffith.mybuddy

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay

class CurrentHydration : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
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
                    Spacer(modifier = if (isLandscape()) Modifier else Modifier.size(30.dp))
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
 *
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
 * A composable function that creates a set of custom buttons with specific labels.
 * The arrangement of the buttons changes based on the screen orientation.
 */
@Composable
fun WaterButtons() {
    val isLandscape = isLandscape()
    val buttonLabels = listOf("Water 250ml", "Water 300ml", "Water 500ml", "Add ml")
    val spacerModifier = if (isLandscape) Modifier else Modifier.size(20.dp)

    Spacer(modifier = spacerModifier)

    Column(verticalArrangement = Arrangement.SpaceEvenly) {
        buttonLabels.forEach { label ->
            WaterButton(label) { /*TODO: Handle onClick*/ }
            Spacer(modifier = if (isLandscape) Modifier.size(5.dp) else Modifier.size(30.dp))
        }
    }
}

@Composable
fun HydrationCircle() {
    val (blueSize, whiteSize) = circleSize()
    Box(modifier = Modifier.size(blueSize), contentAlignment = Alignment.Center) {
        // Bigger Blue Circle
        Canvas(modifier = Modifier.matchParentSize()) {
            drawCircle(color = Color.Blue)
        }

        // Smaller White Circle
        Box(modifier = Modifier.size(whiteSize), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.matchParentSize()) {
                drawCircle(color = Color.White)
            }

            // Text inside the smaller circle
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "0%", style = TextStyle(fontSize = 20.sp))
                Text(text = "0 ml", style = TextStyle(fontSize = 16.sp))
                Text(text = "- 4000 ml", style = TextStyle(fontSize = 16.sp))
            }
        }
    }
}


// A mutable state variable that holds the value of the currently
// selected button. It is initially set to "C".
var selectedButton by mutableStateOf("C")

/**
 * @Composable function to create a row or column of buttons based on the screen orientation.
 */
@Composable
fun MyButtonsRow() {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = if (isLandscape()) Arrangement.Center else Arrangement.Bottom,
        horizontalAlignment = if (isLandscape()) Alignment.End else Alignment.CenterHorizontally
    ) {
        if (!isLandscape()) {
            Divider(color = Color.Blue, thickness = 3.dp)
            Row(
                modifier = Modifier.fillMaxWidth().background(Color.White),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                Spacer(modifier = Modifier.size(30.dp))
                ButtonRowItem("C", CurrentHydration::class.java, context, selectedButton == "C") { selectedButton = "C" }
                Spacer(modifier = Modifier.size(10.dp))
                ButtonRowItem("P", Profile::class.java, context, selectedButton == "P") { selectedButton = "P" }
                Spacer(modifier = Modifier.size(10.dp))
                ButtonRowItem("H", History::class.java, context, selectedButton == "H") { selectedButton = "H" }
                Spacer(modifier = Modifier.size(30.dp))
            }
        } else {
            Column(
                modifier = Modifier.fillMaxHeight().background(Color.White),
                verticalArrangement = Arrangement.SpaceEvenly,
            ) {
                Spacer(modifier = Modifier.size(30.dp))
                ButtonRowItem("C", CurrentHydration::class.java, context, selectedButton == "C") { selectedButton = "C" }
                ButtonRowItem("P", Profile::class.java, context, selectedButton == "P") { selectedButton = "P" }
                ButtonRowItem("H", History::class.java, context, selectedButton == "H") { selectedButton = "H" }
                Spacer(modifier = Modifier.size(5.dp))
            }
        }
    }
}

/**
 * @Composable function to create a button item in the row.
 * @param text The text to display on the button.
 * @param destination The destination class when the button is clicked.
 * @param context The current context.
 * @param isSelected Whether the button is currently selected.
 * @param onSelected The action to perform when the button is selected.
 */
@Composable
fun ButtonRowItem(text: String, destination: Class<*>, context: android.content.Context, isSelected: Boolean, onSelected: () -> Unit) {
    val iconColor = if (isSelected) Color.Blue else Color.Black
    val iconResource = when (text) {
        "C" -> R.drawable.drop
        "P" -> R.drawable.profile
        "H" -> R.drawable.history
        else -> null
    }

    Button(
        onClick = {
            val intent = Intent(context, destination)
            context.startActivity(intent)
            onSelected()
        },
        shape = CircleShape,
        modifier = Modifier.size(70.dp),
        colors = ButtonDefaults.buttonColors(Color.Transparent),
        contentPadding = PaddingValues(0.dp)
    ) {
        if (iconResource != null) {
            IconImage(iconResource, text, iconColor)
        } else {
            Text(text)
        }
    }
}

/**
 * @Composable function to create an image icon.
 * @param resourceId The resource id of the image.
 * @param contentDescription The content description of the image.
 * @param color The color filter to apply to the image.
 */
@Composable
fun IconImage(resourceId: Int, contentDescription: String, color: Color) {
    val size = if (contentDescription == "Logout") 24.dp else 60.dp

    Image(
        painter = painterResource(id = resourceId),
        contentDescription = contentDescription,
        modifier = Modifier.size(size),
        colorFilter = ColorFilter.tint(color)
    )
}

/**
 * This is a composable function that creates a LogOut button. When the button is clicked,
 * an AlertDialog is shown to the user. After a delay, the user is redirected to the Login screen.
 * @param modifier Modifier for styling the LogOut button. Default value is Modifier.
 */
@Composable
fun LogOutButton(modifier: Modifier = Modifier) {
    val showDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text(text = "Log Out") },
            text = { Text(text = "You have been successfully logged out!") },
            confirmButton = { Row { } },
            properties = DialogProperties(dismissOnClickOutside = false)
        )

        LaunchedEffect(showDialog.value) {
            delay(2000)
            showDialog.value = false
            val intent = Intent(context, Login::class.java)
            context.startActivity(intent)
        }
    }

    Button(
        onClick = { showDialog.value = true },
        modifier = modifier.padding(top = 5.dp),
        colors = ButtonDefaults.buttonColors(Color.Transparent),
    ) {
        IconImage(
            R.drawable.logout,
            "Logout",
            Color.Black)
    }
}