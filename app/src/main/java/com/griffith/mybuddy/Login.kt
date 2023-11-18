package com.griffith.mybuddy

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay

/**
 * Julia Dobrovodska
 * 3061278
 * https://github.com/DziJulia/Android-Project-3061278
 */
class Login : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SetupUI()
        }
    }

    /**
     * Responds to changes in the device's configuration, such as when the orientation is changed.
     * @param newConfig The new configuration that the system has changed to.
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setContent {
            SetupUI()
        }
    }
}

val activityBackground = Modifier.background(color = Color(232, 244, 248))

/**
 * This function sets up the UI for the login screen. It adjusts the layout
 * based on the orientation of the device.
 */
@Composable
fun SetupUI() {
    val context = LocalContext.current

    if (!isLandscape()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(200.dp)
                )
                NameField()
                PasswordField()
                Row {
                    Text("Forgot Password") // This will be a link in the future.
                    Spacer(modifier = Modifier.padding(horizontal = 10.dp))
                    Button(onClick = {
                        val intent = Intent(context, CurrentHydration::class.java)
                        context.startActivity(intent)
                    }) {
                        Text("Login")
                    }
                }
                Spacer(modifier = Modifier.padding(vertical = 10.dp))
                Button(onClick = { /* Handle registration either pop up or new activity*/ }) {
                    Text("Register")
                }
            }
        }
    } else if (isLandscape()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxSize()
                    .offset(x = 50.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(300.dp)
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    NameField()
                    PasswordField()
                    Row {
                        Text("Forgot Password") // This will be a link in the future.
                        Spacer(modifier = Modifier.padding(horizontal = 10.dp))
                        Button(onClick = {
                            val intent = Intent(context, CurrentHydration::class.java)
                            context.startActivity(intent)
                        }) {
                            Text("Login")
                        }
                    }
                    Spacer(modifier = Modifier.padding(vertical = 10.dp))
                    Button(onClick = { /* Handle registration either pop up or new activity*/ }) {
                        Text("Register")
                    }
                }
            }
        }
    }
}

/**
 * A composable function to create a name field.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameField() {
    val text = remember { mutableStateOf("") }

    TextField(
        value = text.value,
        onValueChange = { text.value = it },
        label = { Text("Email") }
    )
}

/**
 * A composable function to create a password field.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordField() {
    val text = remember { mutableStateOf("") }

    TextField(
        value = text.value,
        onValueChange = { text.value = it },
        label = { Text("Password") }
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
