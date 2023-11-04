package com.griffith.mybuddy

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

class Profile : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Box(modifier = Modifier.fillMaxSize()) {
                Text("Profile",
                    modifier = Modifier.align(Alignment.TopStart))
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
    Column(modifier = Modifier.padding(16.dp)) {
        val name = remember { mutableStateOf("") }
        TextField(value = name.value, onValueChange = { name.value = it }, label = { Text("Name") })

        val gender = remember { mutableStateOf("") }
        TextField(value = gender.value, onValueChange = { gender.value = it }, label = { Text("Gender") })

        val weight = remember { mutableStateOf("") }
        TextField(value = weight.value, onValueChange = { weight.value = it }, label = { Text("Weight") })

        val height = remember { mutableStateOf("") }
        TextField(value = height.value, onValueChange = { height.value = it }, label = { Text("Height") })

        val activityLevel = remember { mutableStateOf("") }
        TextField(value = activityLevel.value, onValueChange = { activityLevel.value = it }, label = { Text("Activity Level") })

        Text("Water Intake Goal")
        val waterIntakeGoal = remember { mutableStateOf("") }
        TextField(value = waterIntakeGoal.value, onValueChange = { waterIntakeGoal.value = it }, label = { Text("in ml") })
    }
}
