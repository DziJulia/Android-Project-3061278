package com.griffith.mybuddy

import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import java.time.LocalDate
import kotlin.math.roundToInt

/**
 * Julia Dobrovodska
 * 3061278
 * https://github.com/DziJulia/Android-Project-3061278
 */

private lateinit var databaseManager: DatabaseManager
private lateinit var database: SQLiteDatabase

class Profile : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Box(modifier = Modifier.fillMaxSize()) {
                MyButtonsRow()
                LogOutButton(modifier = Modifier.align(Alignment.TopEnd))
                Column(modifier = Modifier.padding(16.dp, end = if (isLandscape()) 70.dp else 0.dp)) {
                    AddSpacer(20.dp)
                    Text(
                        "Profile",
                        style = TextStyle(fontSize = 30.sp, fontWeight = FontWeight.Bold)
                    )
                    ProfileForm()
                }
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()

        val dateString = LocalDate.now().format(AppVariables.formatter)
        // Get the instance of databaseManager
        databaseManager = DatabaseManagerSingleton.getInstance(this)
        // Re-open the database connection in onResume
        database = databaseManager.writableDatabase

        // Load the user profile from the database
        val userProfile = databaseManager.getUserProfile(AppVariables.emailAddress.value)
        val hydrationTable = databaseManager.fetchHydrationData(AppVariables.emailAddress.value, dateString)
        Log.d("RETRIEVED", "userProfile: $userProfile")

        if (userProfile != null) {
            // Update your fields here
            AppVariables.name.value = userProfile.name ?: ""
            AppVariables.gender.value = userProfile.gender ?: ""
            AppVariables.activityLevel.value = userProfile.activityLevel ?: ""
            AppVariables.height.value = userProfile.height.toString()
            AppVariables.weight.value = userProfile.weight.toString()
        }

        //Retrieve values for hydration levels
        if (hydrationTable != Pair(null, null)) {
            val (goal, value) = hydrationTable
            AppVariables.hydrationGoal.value = goal.toString()
            AppVariables.hydrationLevel = value ?: 0
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStop() {
        super.onStop()

        // Update the user profile in the database
        databaseManager.upsertUserProfile(
            AppVariables.emailAddress.value,
            AppVariables.name.value,
            AppVariables.gender.value,
            AppVariables.activityLevel.value,
            AppVariables.height.value.toFloat(),
            AppVariables.weight.value.toFloat()
        )
        CommonFun.updateHydrationData(databaseManager)
    }
}

/**
 * Creates a form with fields for name, gender, weight, height, activity level, and water intake goal.
 */
@Composable
fun ProfileForm() {
    val scrollState = rememberScrollState()
    val formBackground =  Modifier.fillMaxSize().border(width = 3.dp, color = Color.LightGray)

    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AddSpacer(size = 15.dp)
        Card(modifier = formBackground) {
            CreateFormFields()
        }

        AddSpacer(size = 30.dp)

        Text("Water Intake Goal", style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold))
        AddSpacer(size = 6.dp)
        HandleHydrationGoal(formBackground)
    }
}

/**
 * @param size Size of the spacer
 * Adds a spacer to the form
 */
@Composable
fun AddSpacer(size: Dp) {
    Spacer(modifier = Modifier.size(size))
}

/**
 * Adds a divider to the form
 */
@Composable
fun AddDivider() {
    Divider(
        modifier = Modifier.padding(horizontal = 20.dp),
        color = Color.LightGray,
        thickness = 1.dp
    )
}

/**
 * Creates form fields
 */
@Composable
fun CreateFormFields() {
    FormField(label = "Name", value = AppVariables.name)
    AddDivider()

    FormField(label = "Gender", value = AppVariables.gender)
    AddDivider()

    FormField(label = "Weight", value = AppVariables.weight )
    AddDivider()

    FormField(label = "Height", value = AppVariables.height)
    AddDivider()

    FormField(label = "Activity Level", value = AppVariables.activityLevel)
}


/**
 * @param modifier Modifier for form background
 * Handles hydration goal
 */
@Composable
fun HandleHydrationGoal(modifier: Modifier) {
    LocationUpdates { location ->
        AppVariables.altitude = location.altitude
    }

    val activityLevelTransformed = transformActivityLevel(AppVariables.activityLevel.value)
    if (!AppVariables.hydrationGoalManuallySet.value) {
        AppVariables.hydrationGoal.value = calculateRecommendedWaterIntake(
            activityLevelTransformed,
            AppVariables.altitude
        )
    }

    Card(modifier = modifier ) {
        HandleHydrationGoalFormField()
    }

    if (AppVariables.hydrationGoalManuallySet.value) {
        Button(
            onClick = { AppVariables.hydrationGoalManuallySet.value = false },
            colors = ButtonDefaults.buttonColors(Color.Transparent),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text("Recalculate", color = Color.Blue)
        }
    }
}

/**
 * Handles hydration goal form field
 */
@Composable
fun HandleHydrationGoalFormField() {
    FormField(
        label = "Water Intake Goal",
        value = AppVariables.hydrationGoal,
        onValueChange = {
            //need to catch if input value is not nil
            try {
                if (it.toInt() != 0) {
                    AppVariables.hydrationGoal.value = it
                    AppVariables.hydrationGoalManuallySet.value = true
                }
            } catch (e: Exception) {
                AppVariables.hydrationGoal.value = "0"
            }
        }
    )
}

/**
 * Transforms the activity level from a string to a corresponding float value.
 * @param activityLevel The activity level as a string. It can be "Highly Active", "Moderately Active", "Lightly Active", or any other string.
 * @return The corresponding float value for the activity level. It returns HIGHLY_ACTIVE for "Highly Active", MODERATELY_ACTIVE for "Moderately Active", LIGHTLY_ACTIVE for "Lightly Active", and NO_ACTIVE for any other string.
 */
fun transformActivityLevel(activityLevel: String): Float {
    return when (activityLevel) {
        "Highly Active" -> Constants.HIGHLY_ACTIVE
        "Moderately Active" -> Constants.MODERATELY_ACTIVE
        "Lightly Active" -> Constants.LIGHTLY_ACTIVE
        else -> Constants.NO_ACTIVE
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
 * @param activityLevel The activity level of the person. Higher values indicate higher activity levels.
 * @param altitude The altitude from current location.
 * @return The recommended water intake in milliliters.
 */
fun calculateRecommendedWaterIntake(activityLevel: Float, altitude: Double): String {
    val genderFactor = if (AppVariables.gender.value.lowercase() == "female") 0.8f else 0.85f
    val adjustmentFactor = 1.0 + (altitude / 10000.0)
    val result = (AppVariables.weight.value.toFloat()/30 + AppVariables.height.value.toFloat() / 100) * 1000 * genderFactor + activityLevel

    // Return the rounded result directly
    return (result * adjustmentFactor).roundToInt().toString()
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
                            AppVariables.hydrationGoalManuallySet.value = true
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
                colors = ButtonDefaults.buttonColors(colorResource(id = R.color.deepSkyBlueColor))
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
                colors = ButtonDefaults.buttonColors(colorResource(id = R.color.deepSkyBlueColor))
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
                modifier = Modifier.align(Alignment.End).padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    colorResource(id = R.color.deepSkyBlueColor),
                    contentColor = Color.Black
                )
            ) {
                Text("Confirm")
            }
        }
    }
}

