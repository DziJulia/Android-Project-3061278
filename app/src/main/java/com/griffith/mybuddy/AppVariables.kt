package com.griffith.mybuddy

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

/**
 * This object holds the global variables for the application.
 * These variables are accessible across all activities.
 */
object AppVariables {
    /**
     * `weight, height, hydrationGoal` is a mutable state that holds the current information of user.
     * It's an integer that increases based on the amount of water intake.
     * This state is observed by Jetpack Compose and any changes to this state
     * will recompose all composable that read this state.
     */
    var weight =  mutableStateOf("0")
    var height =  mutableStateOf("0")
    var hydrationGoal = mutableStateOf("3000")

    /**
     * `name` is a mutable state that holds the current information of user.
     * It's an string. This state is observed by Jetpack Compose and any changes to this state
     * will recompose all composable that read this state.
     */
    val name = mutableStateOf("")

    /**
     * `activityLevel, gender` is a mutable state that holds the current information of user.
     * It's an string. This state is observed by Jetpack Compose and any changes to this state
     * will recompose all composable that read this state.
     */
    var activityLevel =  mutableStateOf("")
    var gender = mutableStateOf("")
    /**
     * `hydrationGoalManuallySet` is a mutable state that holds boolean that check for user input.
     */
    var hydrationGoalManuallySet = mutableStateOf(false)
    /**
     *  A mutable state variable that holds the value of the currently
     *  selected button. It is initially set to "C".
     */
    var selectedButtonMenu by mutableStateOf("C")

    /**
     * `hydrationLevel` is a mutable state that holds the current hydration level.
     * It's an integer that increases based on the amount of water intake.
     * This state is observed by Jetpack Compose and any changes to this state
     * will recompose all composables that read this state.
     */
    var hydrationLevel by mutableIntStateOf(0)

    /**
     * `hydrationLevelData` is a mutable state that holds the hydrationLevelData.
     * It's an integer that increases based on the amount of water intake.
     * This state is observed by Jetpack Compose and any changes to this state
     * will recompose all composables that read this state.
     */
    var hydrationLevelData by mutableIntStateOf(0)

    /**
     * `hydrationGoalData` is a mutable state that holds the hydrationGoalData.
     * It's an integer that increases based on the amount of water intake.
     * This state is observed by Jetpack Compose and any changes to this state
     * will recompose all composables that read this state.
     */
    var hydrationGoalData by mutableIntStateOf(0)

    /**
     * `showDialog` is a mutable state that controls the visibility of the dialog.
     * When `showDialog` is true, the dialog is visible. When `showDialog` is false,
     * the dialog is dismissed. This state is observed by Jetpack Compose and any changes
     * to this state will recompose all composables that read this state.
     */
    var showDialog by mutableStateOf(false)

    /**
     * This variable holds the altitude value.
     * It's a mutable state, so it can be observed for changes.
     */
    var altitude by mutableDoubleStateOf(0.0)

    /**
     * `isForgotPasswordPopupVisible` is a mutable state that controls the visibility of the dialog.
     * When `isForgotPasswordPopupVisible` is true, the dialog is visible. When `isForgotPasswordPopupVisible` is false,
     * the dialog is dismissed. This state is observed by Jetpack Compose and any changes
     * to this state will recompose all composables that read this state.
     */
    var isForgotPasswordPopupVisible by mutableStateOf(false)

    /**
     * This variable holds the email address.
     * It's a mutable state, so it can be observed for changes.
     */
    val emailAddress = mutableStateOf("")

    /**
     * This variable holds the boolean for registration
     * It's a mutable state, so it can be observed for changes.
     */
    var registration by mutableStateOf(false)

    /**
     * This variable holds the email address for registration
     * It's a mutable state, so it can be observed for changes.
     */
    val emailAddressRegistration = mutableStateOf("")

    /**
     * This variable holds the password for registration
     * It's a mutable state, so it can be observed for changes.
     */
    val password1 = mutableStateOf("")

    /**
     * This variable holds the password for registration comparing
     * It's a mutable state, so it can be observed for changes.
     */
    val password2 = mutableStateOf("")

    /**
     * A DateTimeFormatter that formats or parses a date without a time zone,
     * such as '2023-12-03'. Requires API level 26 and above (Android 8.0, Oreo).
     *
     * @RequiresApi(Build.VERSION_CODES.O)
     */
    @RequiresApi(Build.VERSION_CODES.O)
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    /**
     * A SimpleDateFormat that formats or parses a date without a time zone,
     * such as '2023-12-03', for the US locale.
     */
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    /**
     * A string representing the current date in the format 'yyyy-MM-dd'.
     * Requires API level 26 and above (Android 8.0, Oreo).
     *
     * @RequiresApi(Build.VERSION_CODES.O)
     */
    @RequiresApi(Build.VERSION_CODES.O)
    val dateString: String = LocalDate.now().format(formatter)

    /**
     * A mutable state holding a string representing a period of time.
     */
    val period = mutableStateOf("day")

    /**
     * A mutable state holding a Date object representing a newly selected date.
     */
    val newSelectedDate = mutableStateOf(Date())
}