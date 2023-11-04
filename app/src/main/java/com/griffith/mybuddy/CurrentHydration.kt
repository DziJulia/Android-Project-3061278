package com.griffith.mybuddy

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay

class CurrentHydration : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Box(modifier = Modifier.fillMaxSize()) {
                Text("Current Hydration",
                    modifier = Modifier.align(Alignment.Center))
                val context = LocalContext.current
                val isPortrait = context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
                MyButtonsRow(isPortrait = isPortrait)
                LogOutButton(modifier = Modifier.align(Alignment.TopEnd))
            }
        }
    }
}


// A mutable state variable that holds the value of the currently
// selected button. It is initially set to "C".
var selectedButton by mutableStateOf("C")

/**
 * @Composable function to create a row or column of buttons based on the screen orientation.
 * @param isPortrait Flag indicating whether the device is in portrait mode.
 */
@Composable
fun MyButtonsRow(isPortrait: Boolean) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = if (isPortrait) Arrangement.Bottom else Arrangement.Center,
        horizontalAlignment = if (isPortrait) Alignment.CenterHorizontally else Alignment.End
    ) {
        if (isPortrait) {
            Divider(color = Color.Blue, thickness = 3.dp)
            Row(
                modifier = Modifier.padding(10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                ButtonRowItem("C", CurrentHydration::class.java, context, selectedButton == "C") { selectedButton = "C" }
                Spacer(modifier = Modifier.size(30.dp))
                ButtonRowItem("P", Profile::class.java, context, selectedButton == "P") { selectedButton = "P" }
                Spacer(modifier = Modifier.size(30.dp))
                ButtonRowItem("H", History::class.java, context, selectedButton == "H") { selectedButton = "H" }
            }
        } else {
            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.SpaceEvenly,
            ) {
                ButtonRowItem("C", CurrentHydration::class.java, context, selectedButton == "C") { selectedButton = "C" }
                Spacer(modifier = Modifier.size(10.dp))
                ButtonRowItem("P", Profile::class.java, context, selectedButton == "P") { selectedButton = "P" }
                Spacer(modifier = Modifier.size(10.dp))
                ButtonRowItem("H", History::class.java, context, selectedButton == "H") { selectedButton = "H" }
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
        modifier = modifier.padding(top = 10.dp, end = 10.dp),
        colors = ButtonDefaults.buttonColors(Color.Transparent),
    ) {
        IconImage(
            R.drawable.logout,
            "Logout",
            Color.Gray)
    }
}