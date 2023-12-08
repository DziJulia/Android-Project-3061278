package com.griffith.mybuddy

import android.content.res.Configuration
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * The `CommonFun` object encapsulates all common functions used across the application.
 *
 * This object provides a centralized location for functions that are used in multiple places in the codebase,
 * promoting code reuse and maintainability.
 */
object CommonFun {
    /**
     * Updates the hydration data in the database for the current user and date.
     *
     * @param databaseManager The instance of DatabaseManager to interact with the database.
     * @RequiresApi(Build.VERSION_CODES.O) Specifies that this function requires API level 26 or higher.
     *
     * This function updates the hydration table in the database with the current app variables:
     * - email address
     * - date
     * - hydration goal
     * - hydration level
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateHydrationData(databaseManager: DatabaseManager) {
        CoroutineScope(Dispatchers.IO).launch {
            databaseManager.updateHydrationTable(
                AppVariables.emailAddress.value,
                AppVariables.dateString,
                AppVariables.hydrationGoal.value.toInt(),
                AppVariables.hydrationLevel
            )
            Log.d("hydrationTable", "hydrationLevelDATA: ${AppVariables.hydrationLevel}")
        }
    }


    /**
     * Function that checks whether the current device orientation is landscape.
     * @return true if the device is in landscape orientation, false otherwise.
     */
    @Composable
    fun isLandscape(): Boolean {
        return LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

    /**
     * A composable function that creates a column with a logo and additional content.
     * @param content A lambda representing the additional content to be displayed under the logo.
     */
    @Composable
    fun LogoColumn(content: @Composable () -> Unit) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
            )
            content()
        }
    }

    /**
     * A composable function that starts a countdown from 60 seconds.
     * The countdown is displayed as text and updates every second.
     * When the countdown reaches zero, it calls the provided `onCountdownOver` function.
     *
     * @param onCountdownOver A function to be invoked when the countdown is over.
     */
    @Composable
    fun StartCountdown(onCountdownOver: () -> Unit) {
        var timeLeft by remember { mutableIntStateOf(60) }
        LaunchedEffect(key1 = timeLeft) {
            while (timeLeft > 0) {
                delay(1000L)
                timeLeft--
            }
            onCountdownOver()
        }
        Text("Time left: $timeLeft seconds")
    }


    /**
     * This is a `@Composable` function that defines custom colors for a button in Jetpack Compose.
     * @return ButtonColors object with the background color set to `deepSkyBlueColor` (defined in your color resources)
     * and the content color set to black.
     */
    @Composable
    fun CustomButtonColors(): ButtonColors {
        return ButtonDefaults.buttonColors(
            colorResource(id = R.color.deepSkyBlueColor),
            contentColor = colorResource(id = R.color.black)
        )
    }
}