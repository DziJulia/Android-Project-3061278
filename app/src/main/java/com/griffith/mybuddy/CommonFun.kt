package com.griffith.mybuddy

import android.os.Build
import androidx.annotation.RequiresApi

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
    }
}