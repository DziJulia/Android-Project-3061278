package com.griffith.mybuddy

import android.content.res.Configuration
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

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
        databaseManager.updateHydrationTable(
            AppVariables.emailAddress.value,
            AppVariables.dateString,
            AppVariables.hydrationGoal.value.toInt(),
            AppVariables.hydrationLevel
        )
        Log.d("hydrationTable", "hydrationLevelDATA: ${AppVariables.hydrationLevel}")
    }

    /**
     * Function that checks whether the current device orientation is landscape.
     * @return true if the device is in landscape orientation, false otherwise.
     */
    @Composable
    fun isLandscape(): Boolean {
        return LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    }
}