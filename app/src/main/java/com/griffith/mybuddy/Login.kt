package com.griffith.mybuddy

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

class Login : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            //TODO need to  size app the logo better
            //TODO need to figure out Forgot Password
            //TODO LOGIN verificate through database email and password
            //TODO register page to create user in database ,validation of input
            //TODO make sure registration have strong password maybe option for google login..
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(painter = painterResource(id = R.drawable.logo), contentDescription = "App Logo")
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
