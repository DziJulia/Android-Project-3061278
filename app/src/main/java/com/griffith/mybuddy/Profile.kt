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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
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
            Box(modifier = Modifier.fillMaxSize().then(activityBackground)) {
                val context = LocalContext.current
                val isPortrait = context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
                MyButtonsRow(isPortrait = isPortrait)
                LogOutButton(modifier = Modifier.align(Alignment.TopEnd))
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Profile",
                        style = TextStyle(fontSize = 30.sp,fontWeight = FontWeight.Bold)
                    )
                    ProfileForm()
                }
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
        .border(width = 3.dp, color = Color.LightGray)

    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.size(15.dp))
        Card(modifier = formBackground) {
             val name = remember { mutableStateOf("") }
            FormField(label = "Name", value = name)
            Divider(
                modifier = Modifier.padding(horizontal = 20.dp),
                color = Color.LightGray,
                thickness = 1.dp
            )

            val gender = remember { mutableStateOf("") }
            FormField(label = "Gender", value = gender)
            Divider(
                modifier = Modifier.padding(horizontal = 20.dp),
                color = Color.LightGray,
                thickness = 1.dp
            )

            val weight = remember { mutableStateOf("") }
            FormField(label = "Weight", value = weight)
            Divider(
                modifier = Modifier.padding(horizontal = 20.dp),
                color = Color.LightGray,
                thickness = 1.dp
            )

            val height = remember { mutableStateOf("") }
            FormField(label = "Height", value = height)
            Divider(
                modifier = Modifier.padding(horizontal = 20.dp),
                color = Color.LightGray,
                thickness = 1.dp
            )

            val activityLevel = remember { mutableStateOf("") }
            FormField(label = "Activity Level", value = activityLevel)
        }

        Spacer(modifier = Modifier.size(30.dp))

        Text("Water Intake Goal", style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold))
        Spacer(modifier = Modifier.size(6.dp))
        val waterIntakeGoal = remember { mutableStateOf("") }
        Card(modifier = formBackground ) {
            FormField(label = "Water Intake Goal", value = waterIntakeGoal)
        }
    }
}

/**
 * This function creates a form field with a label and a value. It also manages a dialog state.
 * @param label The label for the form field.
 * @param value The value for the form field. It's a mutable state.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormField(label: String, value: MutableState<String>) {
    val openDialog = remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
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
