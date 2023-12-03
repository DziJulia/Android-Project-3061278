package com.griffith.mybuddy

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.security.SecureRandom
import java.util.Base64
import java.security.MessageDigest
import java.util.Calendar

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
    @RequiresApi(Build.VERSION_CODES.O)
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
    @RequiresApi(Build.VERSION_CODES.O)
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
     * - date: Date of the hydration record Primary key.
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
            date TEXT,
            user_id INTEGER,
            value_of_day INTEGER,
            goal INTEGER,
            PRIMARY KEY (date, user_id),
            FOREIGN KEY (user_id) REFERENCES Users(id)
        );
    """

    /**
     * SQL statement for creating the "Users" table in the database.
     *
     * The table includes fields for storing user information, such as email,
     * hashed password, salt, creation timestamp, and deletion timestamp.
     *
     * Table Columns:
     * - id: Unique identifier for each user.
     * - email: The email of the user UNIQUE.
     * - hashed_password: The hashed password of the user.
     * - salt: The salt used for password hashing.
     * - created_at: Date and time when the user account was created.
     * - deleted_at: Date and time when the user account was deleted, default is NULL.
     */
    private val createUsersTable = """
        CREATE TABLE Users (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            email TEXT UNIQUE,
            hashed_password TEXT,
            salt TEXT,
            created_at TEXT,
            deleted_at TEXT DEFAULT NULL
        );
    """

    /**
     * SQL statement for creating the "UserProfile" table in the database.
     *
     * The table includes fields for storing user profile information, such as name, gender,
     * activity level, height, and weight, linked to a user.
     *
     * Table Columns:
     * - user_id: Foreign key referencing the user associated with the profile.
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
            user_id INTEGER PRIMARY KEY,
            name TEXT,
            gender TEXT,
            activity_level TEXT,
            height FLOAT,
            weight FLOAT,
            deleted_at TEXT DEFAULT NULL,
            FOREIGN KEY (user_id) REFERENCES users(id)
        );
    """

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
     * - Email: 'test'
     * - Hashed Password: 'hashed1'
     * - Salt: 'salt1'
     * - Created At: '2022-01-01'
     *
     * @param db The SQLiteDatabase instance for executing SQL statements.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun insertTestUsers(db: SQLiteDatabase?) {
        val salt = generateSalt()
        val hashedPassword = hashPassword("testPassword", salt)

        db?.execSQL("INSERT INTO users (email, hashed_password, salt, created_at) VALUES ('test', '$hashedPassword', '$salt', '2022-01-01')")
    }

    /**
     * Inserts a new user into the "Users" table of the database.
     *
     * This function generates a random salt, hashes the provided password with the salt,
     * and inserts the user's information into the "Users" table. The hashed password and
     * salt are stored to enhance security.
     *
     * @param email The email of the new user.
     * @param hashedPassword The hashed password of the new user.
     * @return The row ID of the newly inserted user, or -1 if an error occurred.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun insertUser(email: String, hashedPassword: String): Long {
        val salt = generateSalt()
        val values = ContentValues().apply {
            put("email", email)
            put("hashed_password", hashPassword(hashedPassword, salt))
            put("salt", salt)
            put("created_at", AppVariables.dateString)
        }

        return writableDatabase.insert("Users", null, values)
    }
    /**
     * Updates the user information in the "Users" table of the database.
     *
     * This function generates a new random salt, hashes the provided new password with the salt,
     * and updates the user's information (hashed password, salt) in the "Users" table.
     * The new hashed password and salt are stored to enhance security.
     *
     * @param email The unique identifier of the user to be updated.
     * @param newHashedPassword The new hashed password for the user.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun updatePassword(email: String, newHashedPassword: String) {
        val salt = generateSalt()
        val values = ContentValues().apply {
            put("hashed_password", hashPassword(newHashedPassword, salt))
            put("salt", salt)
        }

        writableDatabase.update("Users", values, "email=?", arrayOf(email))
    }

    /**
     * This function updates the goal and date in the `HydrationForDay` table in the database.
     *
     * @param email The email of the user for which the goal needs to be updated.
     * @param date The date for which the goal needs to be updated. It is a string in the format 'YYYY-MM-DD'.
     * @param goal The new hydration goal for the specified date. It is an integer representing the amount of water (in milliliters) the user aims to drink.
     * @param value The value user drink for the specified date
     *
     * This function first retrieves the user ID associated with the given email by calling the `getUserIdByEmail` function.
     * Then, it either inserts a new record with the specified date, user ID, and goal, or updates the goal of an existing record with the same date and user ID.
     * The decision between insertion and update is made based on whether a record with the specified date and user ID already exists in the table.
     *
     * Note: This function assumes that `getUserIdByEmail` is a valid function that returns a user ID given an email, and that the provided email corresponds to a valid user in the `Users` table of the database.
     */
    fun updateHydrationTable(email: String, date: String, goal: Int, value: Int) {
        val values = ContentValues().apply {
            put("date", date)
            put("user_id", getUserIdByEmail(readableDatabase, email))
            put("value_of_day", value)
            put("goal", goal)
        }

        writableDatabase.insertWithOnConflict("HydrationForDay", null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    /**
     * Fetches the hydration goal and value of the day for a given user and date range.
     *
     * @param email The email of the user.
     * @param period The period for which the hydration data is to be fetched. It can be "date", "week", "month", or "year".
     * @param date The date of the period for which the hydration data is to be fetched.
     * @return A list of triple of integers where each triple represents the hydration goal and value of the day for a
     * particular date and month if we query year
     * If no matching record is found, returns an empty list.
     */
    fun fetchHydrationDataForPeriod(email: String, period: String, date: String): List<Triple<Int, Int, Int>> {
        val userId = getUserIdByEmail(readableDatabase, email)
        var startDate = date
        var endDate = date
        val month = date.substring(5, 7).toInt()
        Log.d("DATABASE", "start: $date")
        Log.d("DATABASE", "month: $month")

        when (period) {
            "week" -> {
                val cal = Calendar.getInstance()
                cal.time = AppVariables.sdf.parse(date)!!
                cal.firstDayOfWeek = Calendar.MONDAY
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                startDate = AppVariables.sdf.format(cal.time)
                cal.add(Calendar.DATE, 6)
                endDate = AppVariables.sdf.format(cal.time)
            }
            "month" -> {
                // Assuming the date format is "yyyy-MM-dd"
                startDate = date.substring(0, 8) + "01"
                val cal = Calendar.getInstance()
                cal.time = AppVariables.sdf.parse(startDate)!!
                cal.add(Calendar.MONTH, 1)
                cal.add(Calendar.DATE, -1)
                endDate = AppVariables.sdf.format(cal.time)
            }
            "year" -> {
                startDate = date.substring(0, 5) + "01-01"
                endDate = date.substring(0, 5) + "12-31"
            }
        }

        val data = mutableListOf<Triple<Int, Int, Int>>()
        Log.d("DATABASE", "data: $data")

        if (period == "year") {
            val cal = Calendar.getInstance()
            for (i in 1..12) {
                cal.set(Calendar.YEAR, date.substring(0, 4).toInt())
                cal.set(Calendar.MONTH, i - 1)
                cal.set(Calendar.DAY_OF_MONTH, 1)
                startDate = AppVariables.sdf.format(cal.time)
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
                endDate = AppVariables.sdf.format(cal.time)
                fetchAndAddData(userId!!, startDate, endDate, i, data)
            }
        } else {
            fetchAndAddData(userId!!, startDate, endDate, month, data)
        }

        Log.d("DATABASE", "data: $data")
        return data
    }

    /**
     * This function fetches hydration data for a given user and date range, and adds it to a provided list.
     * @param userId The ID of the user for whom the data is being fetched.
     * @param startDate The start date of the period for which the data is being fetched.
     * @param endDate The end date of the period for which the data is being fetched.
     * @param month The month for which the data is being fetched. This is used when adding data to the list.
     * @param data The list to which the fetched data is added. Each element in the list is a Triple containing the hydration goal, the value for the day, and the month.
     */
    private fun fetchAndAddData(userId: Int, startDate: String, endDate: String, month: Int, data: MutableList<Triple<Int, Int, Int>>) {
        val cursor = readableDatabase.query(
            "HydrationForDay",
            arrayOf("goal", "value_of_day"),
            "user_id=? AND date BETWEEN ? AND ?",
            arrayOf(userId.toString(), startDate, endDate),
            null,
            null,
            null
        )

        cursor.use {
            while (it.moveToNext()) {
                val goalIndex = it.getColumnIndex("goal")
                val valueIndex = it.getColumnIndex("value_of_day")
                val goal = if (goalIndex != -1) it.getInt(goalIndex) else 0
                val value = if (valueIndex != -1) it.getInt(valueIndex) else 0
                data.add(Triple(goal, value, month))
            }
        }
    }


    /**
     * Fetches the hydration goal and value of the day for a given user and date.
     *
     * @param email The email of the user.
     * @param date The date for which the hydration data is to be fetched.
     * @return A pair of integers where the first integer is the hydration goal and the second integer is the value of the day.
     * If no matching record is found, returns a pair of null values.
     */
    fun fetchHydrationData(email: String, date: String): Pair<Int?, Int?> {
        val userId = getUserIdByEmail(readableDatabase, email)
        val cursor = readableDatabase.query(
            "HydrationForDay",
            arrayOf("goal", "value_of_day"),
            "user_id=? AND date=?",
            arrayOf(userId.toString(), date),
            null,
            null,
            null
        )

        return cursor.use {
            if (it.moveToFirst()) {
                val goalIndex = it.getColumnIndex("goal")
                val valueIndex = it.getColumnIndex("value_of_day")
                val goal = if (goalIndex != -1) it.getInt(goalIndex) else null
                val value = if (valueIndex != -1) it.getInt(valueIndex) else null
                Log.d("hydrationTable", "Pair(goal, value): ${Pair(goal, value)}")

                Pair(goal, value)
            } else {
                Pair(null, null)
            }
        }
    }

    /**
     * Verifies a login attempt by comparing the entered password with the stored hashed password.
     *
     * @param email The email for the login attempt.
     * @param enteredPassword The password entered by the user for the login attempt.
     * @return `true` if the login is successful; `false` otherwise.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun verifyLogin(email: String, enteredPassword: String): Boolean {
        val (storedEmail, storedHashedPassword, storedSalt) = getUserCredentialsForLogin(email)

        if (storedEmail != null && storedHashedPassword != null && storedSalt != null) {
            val hashedEnteredPassword = hashPassword(enteredPassword, storedSalt)

            return hashedEnteredPassword == storedHashedPassword
        }

        return false
    }

    /**
     * Checks if the provided email is present in the database.
     *
     * This function queries the database for the provided email using the `getUserCredentialsForLogin` function.
     * If the email is found in the database, the function returns `false`. If the email is not found, the function returns `true`.
     *
     * @param email The email to check in the database.
     * @return `false` if the email is found in the database, `true` otherwise.
     */
    fun isEmailPresent(email: String): Boolean {
        val storedEmail = getUserCredentialsForLogin(email).first
        Log.d("Database", "Email Present: $storedEmail")

        if (storedEmail != null) {
            return true
        }

        return false
    }


    /**
     * Retrieves user profile information from the "UserProfile" table.
     *
     * @param email The email of the user
     * @return An instance of UserProfileInfo containing the retrieved user profile information,
     * or null if no profile is found.
     */
    fun getUserProfile(email: String): UserInfo?{
        val db = readableDatabase
        val userId = getUserIdByEmail(db, email)
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
            } else {
                // No user profile found, return null
                return null
            }
        }

        return UserInfo(name, gender, activityLevel, height, weight)
    }

    /**
     * Updates or inserts user profile information into the "UserProfile" table.
     *
     * @param email The email associated with the user.
     * @param name The name associated with the user profile.
     * @param gender The gender of the user.
     * @param activityLevel The activity level of the user.
     * @param height The height of the user.
     * @param weight The weight of the user.
     */
    fun upsertUserProfile(
        email: String,
        name: String,
        gender: String,
        activityLevel: String,
        height: Float,
        weight: Float
    ) {
        val userId = getUserIdByEmail(readableDatabase, email)
        val values = ContentValues().apply {
            put("name", name)
            put("gender", gender)
            put("activity_level", activityLevel)
            put("height", height)
            put("weight", weight)
            put("user_id", userId)
        }

        val affectedRows = writableDatabase.update("UserProfile", values, "user_id = ?", arrayOf(userId.toString()))

        if (affectedRows == 0) {
            writableDatabase.insert("UserProfile", null, values)
        }
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
     * @param email The email of the user to retrieve credentials for.
     * @return A Triple containing the retrieved email, hashed password, and salt.
     */
    private fun getUserCredentialsForLogin(email: String): Triple<String?, String?, String?> {
        val db = readableDatabase
        val cursor = db.query(
            "Users",
            arrayOf("email", "hashed_password", "salt"),
            "email=?",
            arrayOf(email),
            null,
            null,
            null
        )

        var storedEmail: String? = null
        var storedHashedPassword: String? = null
        var storedSalt: String? = null

        cursor.use {
            if (it.moveToFirst()) {
                val getColumnValue: (String) -> String? = { columnName ->
                    val columnIndex = it.getColumnIndex(columnName)
                    if (columnIndex != -1) it.getString(columnIndex) else null
                }

                storedEmail = getColumnValue("email")
                storedHashedPassword = getColumnValue("hashed_password")
                storedSalt = getColumnValue("salt")
            }
        }

        return Triple(storedEmail, storedHashedPassword, storedSalt)
    }

    /**
     * Gets the user_id associated with the given email from the "Users" table.
     *
     * @param db The readable database instance.
     * @param email The email of the user.
     * @return The user_id associated with the email, or null if not found.
     */
    private fun getUserIdByEmail(db: SQLiteDatabase, email: String): Int? {
        val cursor = db.query(
            "Users",
            arrayOf("id"),
            "email=?",
            arrayOf(email),
            null,
            null,
            null
        )

        return cursor.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndex("id")
                if (columnIndex != -1) it.getInt(columnIndex) else null
            } else {
                null
            }
        }
    }
}