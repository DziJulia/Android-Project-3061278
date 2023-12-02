package com.griffith.mybuddy

import android.content.Context

/**
 * Singleton object for managing database operations.
 *
 * This object provides a single global point of access to the DatabaseManager instance.
 * It ensures that only one instance of DatabaseManager is created and shared among other classes in the application.
 * This is particularly useful for resources that are expensive to create, like database connections.
 *
 * @property databaseManager The singleton instance of DatabaseManager.
 */
object DatabaseManagerSingleton {
    private var databaseManager: DatabaseManager? = null

    /**
     * Returns the singleton instance of DatabaseManager, creating it if necessary.
     *
     * @param context The Context in which the database is being opened or created.
     * @return The singleton instance of DatabaseManager.
     */
    fun getInstance(context: Context): DatabaseManager {
        if (databaseManager == null) {
            databaseManager = DatabaseManager(
                context,
                "drink_up.db",
                null,
                1
            )
        }
        return databaseManager!!
    }
}
