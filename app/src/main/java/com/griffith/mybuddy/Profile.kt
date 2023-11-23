package com.griffith.mybuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlin.math.roundToInt

/**
 * Julia Dobrovodska
 * 3061278
 * https://github.com/DziJulia/Android-Project-3061278
 */

const val HIGHLY_ACTIVE = 500f
const val MODERATELY_ACTIVE = 250f
const val LIGHTLY_ACTIVE = 100f
const val NO_ACTIVE = 0f
class Profile : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Box(modifier = Modifier.fillMaxSize().then(activityBackground)) {
                MyButtonsRow()
                LogOutButton(modifier = Modifier.align(Alignment.TopEnd))
                Column(modifier = Modifier.padding(16.dp, end = if (isLandscape()) 70.dp else 0.dp)) {
                    Spacer(modifier = Modifier.size(20.dp))
                    Text(
                        "Profile",
                        style = TextStyle(fontSize = 30.sp, fontWeight = FontWeight.Bold)
                    )
                    ProfileForm()
                }
            }
        }
    }
}

var weight =  mutableStateOf("0")
var height =  mutableStateOf("0")
var activityLevel =  mutableStateOf("")
var hydrationGoal = mutableStateOf("0")
var gender = mutableStateOf("")
var hydrationGoalManuallySet = mutableStateOf(false)



/**
 * Creates a form with fields for name, gender, weight, height, activity level, and water intake goal.
 */
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

            FormField(label = "Gender", value = gender)
            Divider(
                modifier = Modifier.padding(horizontal = 20.dp),
                color = Color.LightGray,
                thickness = 1.dp
            )

            FormField(label = "Weight", value = weight )
            Divider(
                modifier = Modifier.padding(horizontal = 20.dp),
                color = Color.LightGray,
                thickness = 1.dp
            )

            FormField(label = "Height", value = height)
            Divider(
                modifier = Modifier.padding(horizontal = 20.dp),
                color = Color.LightGray,
                thickness = 1.dp
            )

            FormField(label = "Activity Level", value = activityLevel)
        }

        Spacer(modifier = Modifier.size(30.dp))

        Text("Water Intake Goal", style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold))
        Spacer(modifier = Modifier.size(6.dp))
        val activityLevelTransformed = transformActivityLevel(activityLevel.value)
        if (!hydrationGoalManuallySet.value) {
            hydrationGoal.value = calculateRecommendedWaterIntake(
                try {
                    weight.value.toInt()
                } catch (e: NumberFormatException) {
                    0
                },
                try {
                    height.value.toInt()
                } catch (e: NumberFormatException) {
                    0
                },
                activityLevelTransformed,
                gender.value
            )
        }

        Card(modifier = formBackground ) {
            FormField(
                label = "Water Intake Goal",
                value = hydrationGoal,
                onValueChange = { hydrationGoalManuallySet.value = true }
            )
        }

        if (hydrationGoalManuallySet.value) {
            Button(
                onClick = { hydrationGoalManuallySet.value = false },
                colors = ButtonDefaults.buttonColors(Color.Transparent),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("Recalculate", color = Color.Blue)
            }
        }

    }
}

fun transformActivityLevel(activityLevel: String): Float {
    return when (activityLevel) {
        "Highly Active" -> HIGHLY_ACTIVE
        "Moderately Active" -> MODERATELY_ACTIVE
        "Lightly Active" -> LIGHTLY_ACTIVE
        else -> NO_ACTIVE
    }
}


/**
 * This function creates a form field with a label and a value. It also manages a dialog state.
 * @param label The label for the form field.
 * @param value The value for the form field. It's a mutable state.
 * @param onValueChange An optional callback function that is invoked when the value changes.
 * It defaults to an empty function.
 */
@Composable
fun FormField(label: String, value: MutableState<String>, onValueChange: (String) -> Unit = {}) {
    val openDialog = remember { mutableStateOf(false) }
    val text = buildAnnotatedString {
        withStyle(style = SpanStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)) {
            append("$label:   ")
        }
        withStyle(style = SpanStyle(fontSize = 20.sp)) {
            append(value.value)
        }
        if (label == "Height") append(" cm")
        if (label == "Weight") append(" kg")
        if (label == "Water Intake Goal") append(" ml")
        if (label == "Water Intake Recommendation") append(" ml")
    }


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
                text,
                modifier = Modifier.weight(1f)
            )
            Text(">", style = TextStyle(fontSize = 18.sp, textAlign = TextAlign.End))
        }
    }

    if (openDialog.value) {
        when (label) {
            "Gender", "Activity Level" -> ShowSelectionPopup(label, value, openDialog)
            "Height", "Weight", "Water Intake Goal" -> ShowNumberInputDialog(label, value, openDialog, onValueChange)
            else -> ShowTextInputDialog(label, value, openDialog)
        }
    }
}

/**
 * Calculates the recommended water intake for a person based on their weight, height, activity level, and gender.
 * @param weight The weight of the person in kilograms.
 * @param height The height of the person in centimeters.
 * @param activityLevel The activity level of the person. Higher values indicate higher activity levels.
 * @param gender The gender of the person. Can be "male" or "female".
 * @return The recommended water intake in milliliters.
 */
fun calculateRecommendedWaterIntake(weight: Int, height: Int, activityLevel: Float, gender: String): String {
    val genderFactor = if (gender.lowercase() == "female") 0.8f else 0.85f

    val result = ((weight.toFloat() / 30 + height.toFloat() / 100) * 1000 * genderFactor) + activityLevel
    return result.roundToInt().toString()
}


/**
 * This is a composable function that shows a selection popup with a list of options.
 * @param label The label of the form field.
 * @param value The current value of the form field. This is a mutable state that can be updated.
 * @param openDialog The state of the dialog. This is a mutable state that can be updated to open or close the dialog.
 */
@Composable
fun ShowSelectionPopup(label: String, value: MutableState<String>, openDialog: MutableState<Boolean>) {
    val options = if (label == "Gender") listOf("Female", "Male", "Other") else listOf("No Active", "Lightly Active", "Moderately Active", "Highly Active")

    SelectionPopup(label, value, options) {
        openDialog.value = false
    }
}

/**
 * This is a composable function that shows a number input dialog. Validate of numbers only
 * @param label The label of the form field.
 * @param value The current value of the form field. This is a mutable state that can be updated.
 * @param openDialog The state of the dialog. This is a mutable state that can be updated to open or close the dialog.
 * @param onValueChange An optional callback function that is invoked when the value changes.
 * It defaults to an empty function.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowNumberInputDialog(label: String, value: MutableState<String>, openDialog: MutableState<Boolean>, onValueChange: (String) -> Unit) {
    val focusManager = LocalFocusManager.current

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
                onValueChange = { newValue ->
                    if (newValue.all { it.isDigit() }) {
                        value.value = newValue
                        onValueChange(newValue)
                        if (label == "Water Intake Goal") {
                            hydrationGoalManuallySet.value = true
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    openDialog.value = false
                },
                colors = ButtonDefaults.buttonColors(deepSkyBlueColor)
            ) {
                Text(text = "Confirm", color = Color.Black)
            }
        }
    )
}

/**
 * This is a composable function that shows a text input dialog.
 * @param label The label of the form field.
 * @param value The current value of the form field. This is a mutable state that can be updated.
 * @param openDialog The state of the dialog. This is a mutable state that can be updated to open or close the dialog.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowTextInputDialog(label: String, value: MutableState<String>, openDialog: MutableState<Boolean>) {
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
                },
                colors = ButtonDefaults.buttonColors(deepSkyBlueColor)
            ) {
                Text(text = "Confirm", color = Color.Black)
            }
        }
    )
}

/**
 * This is a composable function that shows a selection popup with a list of options.
 * @param label The label of the form field.
 * @param value The current value of the form field. This is a mutable state that can be updated.
 * @param options The list of options that can be selected in the popup.
 * @param onDismiss The function to be called when the popup is dismissed.
 */
@Composable
fun SelectionPopup(label: String, value: MutableState<String>, options: List<String>, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Column ( modifier = Modifier
            .border(2.dp, Color(192, 226, 236))
            .background(Color(232, 244, 248))
        ) {
            Text(text = label, modifier = Modifier.padding(16.dp))
            options.forEach { option ->
                Row(Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (value.value == option),
                        onClick = { value.value = option }
                    )
                    Text(
                        text = option,
                        style = TextStyle(fontSize = 20.sp),
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .clickable { value.value = option }
                    )
                }
            }
            Button(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.End).padding(16.dp)
            ) {
                Text("Confirm")
            }
        }
    }
}

