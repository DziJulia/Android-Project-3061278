package com.griffith.mybuddy

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import androidx.annotation.RequiresApi
import java.security.SecureRandom
import java.util.Base64
import java.security.MessageDigest

/**
 * Manages the creation and upgrading of the SQLite database for the application.
 *
 * @param context The context to use for accessing the application's resources.
 * @param name The name of the database file or null for an in-memory database.
 * @param factory An optional factory class that is called to instantiate a cursor
 *                when querying the database.
 * @param version The version number of the database (starting at 1). If the database
 *                is older, the {@link #onUpgrade} method will be used to upgrade the
 *                database; if the database is newer, the {@link #onDowngrade} method
 *                will be used to downgrade the database.
 */
class DatabaseManager(
    context: Context,
    name: String,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
): SQLiteOpenHelper(context, name, factory, version)
{
    /**
     * Called when the database is created for the first time. This method is responsible for
     * executing SQL statements to create initial tables and insert test data.
     *
     * @param p0 The SQLiteDatabase instance being created.
     */
    override fun onCreate(p0: SQLiteDatabase?) {
        p0?.execSQL(createUsersTable)
        p0?.execSQL(createHydrationTable)
        p0?.execSQL(createUserProfileTable)

        insertTestUsers(p0)
    }

    /**
     * Called when the database needs to be upgraded. This method is responsible for
     * executing SQL statements to drop existing tables and recreate them with updated
     * schemas during a database version upgrade.
     *
     * @param p0 The SQLiteDatabase instance being upgraded.
     * @param p1 The old database version.
     * @param p2 The new database version.
     */
    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        p0?.execSQL(dropHydrationTable)
        p0?.execSQL(dropProfileTable)
        p0?.execSQL(dropUsersTable)

        p0?.execSQL(createUsersTable)
        p0?.execSQL(createHydrationTable)
        p0?.execSQL(createUserProfileTable)

        insertTestUsers(p0)
    }

    /**
     * SQL statement for creating the "HydrationForDay" table in the database.
     *
     * The table includes fields for tracking hydration data for each day, linked to a user.
     *
     * Table Columns:
     * - id: Unique identifier for each hydration record.
     * - date: Date of the hydration record.
     * - value_of_day: Numeric value representing the hydration level for the day.
     * - goal: Numeric value representing the hydration goal for the day.
     * - user_id: Foreign key referencing the user associated with the hydration record.
     * - deleted_at: Date and time when the hydration record was deleted, default is NULL.
     *
     * Foreign Key:
     * - user_id references the id column in the Users table.
     */
    private val createHydrationTable = """
        CREATE TABLE HydrationForDay (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            date TEXT,
            value_of_day INTEGER,
            goal INTEGER,
            user_id INTEGER,
            deleted_at TEXT DEFAULT NULL,
            FOREIGN KEY (user_id) REFERENCES users(id)
        );"""

    /**
     * SQL statement for creating the "Users" table in the database.
     *
     * The table includes fields for storing user information, such as username,
     * hashed password, salt, creation timestamp, and deletion timestamp.
     *
     * Table Columns:
     * - id: Unique identifier for each user.
     * - username: The username of the user.
     * - hashed_password: The hashed password of the user.
     * - salt: The salt used for password hashing.
     * - created_at: Date and time when the user account was created.
     * - deleted_at: Date and time when the user account was deleted, default is NULL.
     */
    private val createUsersTable = """
        CREATE TABLE Users (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            username TEXT,
            hashed_password TEXT,
            salt TEXT,
            created_at TEXT,
            deleted_at TEXT DEFAULT NULL
        );"""

    /**
     * SQL statement for creating the "UserProfile" table in the database.
     *
     * The table includes fields for storing user profile information, such as name, gender,
     * activity level, height, and weight, linked to a user.
     *
     * Table Columns:
     * - id: Unique identifier for each user profile.
     * - name: The name associated with the user profile.
     * - gender: The gender of the user.
     * - activity_level: The activity level of the user.
     * - height: The height of the user in meters.
     * - weight: The weight of the user in kilograms.
     * - user_id: Foreign key referencing the user associated with the profile.
     * - deleted_at: Date and time when the user profile was deleted, default is NULL.
     *
     * Foreign Key:
     * - user_id references the id column in the Users table.
     */
    private val createUserProfileTable = """
        CREATE TABLE UserProfile (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT,
            gender TEXT,
            activity_level TEXT,
            height FLOAT,
            weight FLOAT,
            user_id INTEGER,
            deleted_at TEXT DEFAULT NULL,
            FOREIGN KEY (user_id) REFERENCES users(id)
        );"""

    /**
     * SQL statement for dropping the "Users" table from the database if it exists.
     */
    private val dropUsersTable = "DROP TABLE IF EXISTS Users"
    /**
     * SQL statement for dropping the "HydrationForDay" table from the database if it exists.
     */
    private val dropHydrationTable = "DROP TABLE IF EXISTS HydrationForDay"
    /**
     * SQL statement for dropping the "UserProfile" table from the database if it exists.
     */
    private val dropProfileTable = "DROP TABLE IF EXISTS UserProfile"

    /**
     * Inserts test user values into the "Users" table for testing purposes.
     *
     * This function is used to populate the "Users" table with hardcoded user values
     * during testing. It is typically called during the creation or upgrade of the
     * database to ensure initial data is available for testing.
     *
     * Test User Values:
     * - Username: 'test'
     * - Hashed Password: 'hashed1'
     * - Salt: 'salt1'
     * - Created At: '2022-01-01'
     *
     * @param db The SQLiteDatabase instance for executing SQL statements.
     */
    private fun insertTestUsers(db: SQLiteDatabase?) {
        // Insert hardcoded user values for testing
        db?.execSQL("INSERT INTO users (username, hashed_password, salt, created_at) VALUES ('test', 'hashed1', 'salt1', '2022-01-01')")
    }

    /**
     * Inserts a new user into the "Users" table of the database.
     *
     * This function generates a random salt, hashes the provided password with the salt,
     * and inserts the user's information into the "Users" table. The hashed password and
     * salt are stored to enhance security.
     *
     * @param username The username of the new user.
     * @param hashedPassword The hashed password of the new user.
     * @param createdAt The timestamp indicating when the user account was created.
     * @return The row ID of the newly inserted user, or -1 if an error occurred.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun insertUser(username: String, hashedPassword: String, createdAt: String): Long {
        val values = ContentValues()
        val salt = generateSalt()
        val populatedHashedPassword = hashPassword(hashedPassword, salt)
        values.put("username", username)
        values.put("hashed_password", populatedHashedPassword)
        values.put("salt", salt)
        values.put("created_at", createdAt)

        val db = writableDatabase
        return db.insert("Users", null, values)
    }

    /**
     * Updates the user information in the "Users" table of the database.
     *
     * This function generates a new random salt, hashes the provided new password with the salt,
     * and updates the user's information (username, hashed password, salt) in the "Users" table.
     * The new hashed password and salt are stored to enhance security.
     *
     * @param userId The unique identifier of the user to be updated.
     * @param newUsername The new username for the user.
     * @param newHashedPassword The new hashed password for the user.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateUser(userId: Long, newUsername: String, newHashedPassword: String) {
        val values = ContentValues()
        val salt = generateSalt()
        val hashedPassword = hashPassword(newHashedPassword, salt)
        values.put("username", newUsername)
        values.put("hashed_password", hashedPassword)
        values.put("salt", salt)

        val db = writableDatabase
        db.update("Users", values, "id=?", arrayOf(userId.toString()))
    }

    /**
     * Verifies a login attempt by comparing the entered password with the stored hashed password.
     *
     * @param username The username for the login attempt.
     * @param enteredPassword The password entered by the user for the login attempt.
     * @return `true` if the login is successful; `false` otherwise.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun verifyLogin(username: String, enteredPassword: String): Boolean {
        val (storedUsername, storedHashedPassword, storedSalt) = getUserCredentialsForLogin(username)

        if (storedUsername != null && storedHashedPassword != null && storedSalt != null) {
            val hashedEnteredPassword = hashPassword(enteredPassword, storedSalt)

            return hashedEnteredPassword == storedHashedPassword
        }

        return false
    }

    /**
     * Retrieves user profile information from the "UserProfile" table.
     *
     * @param userId The unique identifier of the user whose profile information is to be retrieved.
     * @return An instance of UserProfileInfo containing the retrieved user profile information.
     */
    fun getUserProfile(userId: Long): UserInfo{
        val db = readableDatabase
        val cursor = db.query(
            "UserProfile",
            arrayOf("name", "gender", "activity_level","height","weight"),
            "user_id=?",
            arrayOf(userId.toString()),
            null,
            null,
            null
        )

        var name: String? = null
        var gender: String? = null
        var activityLevel: String? = null
        var height: Float? = null
        var weight: Float? = null

        cursor.use {
            if (it.moveToFirst()) {
                val getColumnValue: (String) -> String? = { columnName ->
                    val columnIndex = it.getColumnIndex(columnName)
                    if (columnIndex != -1) it.getString(columnIndex) else null
                }

                name = getColumnValue("name")
                gender = getColumnValue("gender")
                activityLevel = getColumnValue("activity_level")
                height = getColumnValue("height")?.toFloatOrNull()
                weight = getColumnValue("weight")?.toFloatOrNull()
            }
        }

        return UserInfo(name, gender, activityLevel,height,weight)
    }

    /**
     * Inserts user profile information into the "UserProfile" table.
     *
     * @param userId The unique identifier of the user to whom the profile information belongs.
     * @param name The name associated with the user profile.
     * @param gender The gender of the user.
     * @param activityLevel The activity level of the user.
     */
    fun insertUserProfile(
        userId: Long,
        name: String,
        gender: String,
        activityLevel: String,
        height: Float,
        weight: Float
    ) {
        val values = ContentValues().apply {
            put("name", name)
            put("gender", gender)
            put("activity_level", activityLevel)
            put("height", height)
            put("weight", weight)
            put("user_id", userId)
        }

        val db = writableDatabase
        db.insert("UserProfile", null, values)
    }


    /**
     * Hashes a password with a salt using the SHA-256 algorithm.
     * @return The hashed password as a Base64-encoded string.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun generateSalt(): String {
        val random = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)
        return Base64.getEncoder().encodeToString(salt)
    }

    /**
     * Hashes a password with a salt using the SHA-256 algorithm.
     *
     * @param password The password to be hashed.
     * @param salt The salt to be used for hashing the password.
     * @return The hashed password as a Base64-encoded string.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun hashPassword(password: String, salt: String): String {
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val saltedPassword = password + salt
        val hashedBytes = messageDigest.digest(saltedPassword.toByteArray())

        return Base64.getEncoder().encodeToString(hashedBytes)
    }

    /**
     * Retrieves user credentials for login from the "Users" table.
     *
     * @param username The username of the user to retrieve credentials for.
     * @return A Triple containing the retrieved username, hashed password, and salt.
     */
    private fun getUserCredentialsForLogin(username: String): Triple<String?, String?, String?> {
        val db = readableDatabase
        val cursor = db.query(
            "Users",
            arrayOf("username", "hashed_password", "salt"),
            "username=?",
            arrayOf(username),
            null,
            null,
            null
        )

        var storedUsername: String? = null
        var storedHashedPassword: String? = null
        var storedSalt: String? = null

        cursor.use {
            if (it.moveToFirst()) {
                val getColumnValue: (String) -> String? = { columnName ->
                    val columnIndex = it.getColumnIndex(columnName)
                    if (columnIndex != -1) it.getString(columnIndex) else null
                }

                storedUsername = getColumnValue("username")
                storedHashedPassword = getColumnValue("hashed_password")
                storedSalt = getColumnValue("salt")
            }
        }

        return Triple(storedUsername, storedHashedPassword, storedSalt)
    }
}