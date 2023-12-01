package com.griffith.mybuddy

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

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
}