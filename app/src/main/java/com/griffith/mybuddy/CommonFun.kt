package com.griffith.mybuddy

import android.content.res.Configuration
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

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
    fun customButtonColors(): ButtonColors {
        return ButtonDefaults.buttonColors(
            colorResource(id = R.color.deepSkyBlueColor),
            contentColor = colorResource(id = R.color.black)
        )
    }

    /**
     * This is a `@Composable` function that defines custom colors for a button in Jetpack Compose.
     * @return ButtonColors object with the background color set to `transparent` (defined in your color resources)
     * and the content color set to black.
     */
    @Composable
    fun transparentButtonColors(): ButtonColors {
        return ButtonDefaults.buttonColors(
            Color.Transparent,
            contentColor = Color.Black
        )
    }

    /**
     * Calculates the time until midnight in milliseconds.
     * This function creates a Calendar instance representing the next midnight and subtracts the current time from it to get the time until midnight.
     * @return The time until midnight in milliseconds.
     */
    fun getTimeUntilMidnight(): Long {
        val calendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val currentTime = Calendar.getInstance().timeInMillis
        return calendar.timeInMillis - currentTime
    }

    /**
     * This is a composable function that returns a map of parameters for an AlertDialog.
     *
     * @param containerColor The color of the container. Default is the color resource 'activityBackground'.
     * @param titleContentColor The color of the title content. Default is the color resource 'veryDarkBlue'.
     * @param modifier The modifier for the AlertDialog. Default is a background with the color resource 'activityBackground'.
     *
     * @return A map containing the parameters for the AlertDialog.
     */
    @Composable
    fun alertDialogParameters(
        containerColor: Color = colorResource(id = R.color.activityBackground),
        titleContentColor: Color = colorResource(id = R.color.veryDarkBlue),
        modifier: Modifier = Modifier.background(color = colorResource(id = R.color.activityBackground))
    ): Map<String, Any> {
        return mapOf(
            "containerColor" to containerColor,
            "titleContentColor" to titleContentColor,
            "modifier" to modifier
        )
    }
}