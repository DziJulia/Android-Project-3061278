package com.griffith.mybuddy

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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

    @RequiresApi(Build.VERSION_CODES.O)
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    @RequiresApi(Build.VERSION_CODES.O)
    val dateString = LocalDate.now().format(formatter)
}