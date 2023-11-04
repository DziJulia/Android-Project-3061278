package com.griffith.mybuddy

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class Profile : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val activityBackground = Modifier.background(color = Color(232, 244, 248))

            Box(modifier = Modifier.fillMaxSize().then(activityBackground)) {
                val context = LocalContext.current
                val isPortrait = context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
                MyButtonsRow(isPortrait = isPortrait)
                LogOutButton(modifier = Modifier.align(Alignment.TopEnd))
                ProfileForm()
            }
        }
    }
}

/**
 * Creates a form with fields for name, gender, weight, height, activity level, and water intake goal.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileForm() {
    val scrollState = rememberScrollState()
    val formBackground = Modifier
        .fillMaxSize()
        .background(Color.White)
        .border(width = 3.dp, color = Color.Gray)

    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Profile", style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold))

        Card(modifier = formBackground) {
             val name = remember { mutableStateOf("") }
            FormField(label = "Name", value = name)
            Divider(color = Color.Blue, thickness = 1.dp)

            val gender = remember { mutableStateOf("") }
            FormField(label = "Gender", value = gender)
            Divider(color = Color.Blue, thickness = 1.dp)

            val weight = remember { mutableStateOf("") }
            FormField(label = "Weight", value = weight)
            Divider(color = Color.Blue, thickness = 1.dp)

            val height = remember { mutableStateOf("") }
            FormField(label = "Height", value = height)
            Divider(color = Color.Blue, thickness = 1.dp)

            val activityLevel = remember { mutableStateOf("") }
            FormField(label = "Activity Level", value = activityLevel)
        }

        Spacer(modifier = Modifier.size(30.dp))

        Text("Water Intake Goal", style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold))
        val waterIntakeGoal = remember { mutableStateOf("") }
        FormField(label = "Water Intake Goal", value = waterIntakeGoal)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormField(label: String, value: MutableState<String>) {
    val openDialog = remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { openDialog.value = true }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                "$label: ${value.value}",
                style = TextStyle(fontSize = 20.sp),
                modifier = Modifier.weight(1f)
            )
            Text(">", style = TextStyle(fontSize = 20.sp, textAlign = TextAlign.End))
        }
    }

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            title = {
                Text(text = label)
            },
            text = {
                TextField(
                    value = value.value,
                    onValueChange = { newValue -> value.value = newValue }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        openDialog.value = false
                    }
                ) {
                    Text("Confirm")
                }
            }
        )
    }
}
