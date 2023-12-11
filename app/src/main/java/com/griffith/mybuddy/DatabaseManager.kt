package com.griffith.mybuddy

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.security.SecureRandom
import java.util.Base64
import java.security.MessageDigest
import java.time.LocalDate
import java.time.Year
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Random

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
): SQLiteOpenHelper(context, name, factory, version) {
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

        CoroutineScope(Dispatchers.IO).launch {
            insertTestUsers(p0)
            insertTestData(p0)
        }
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

        CoroutineScope(Dispatchers.IO).launch {
            insertTestUsers(p0)
            insertTestData(p0)
        }
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
     * - recalculate The recalculate should be visible or not
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
            recalculate BOOLEAN DEFAULT 'FALSE',
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
        val userId = getUserIdByEmail(readableDatabase, email) ?: return

        val values = ContentValues().apply {
            put("date", date)
            put("user_id", userId)
            put("value_of_day", value)
            put("goal", goal)
        }

        writableDatabase.insertWithOnConflict(
            "HydrationForDay",
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE
        )
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
    fun fetchHydrationDataForPeriod(
        email: String,
        period: String,
        date: String
    ): List<Triple<Int, Int, Int>> {
        val userId = getUserIdByEmail(readableDatabase, email) ?: return emptyList()

        var startDate = date
        Log.d("DATABASE", "  startDate: $startDate")
        val cal = Calendar.getInstance()
        cal.time = AppVariables.sdf.parse(date)!!

        val data = mutableListOf<Triple<Int, Int, Int>>()

        when (period) {
            "week" -> {
                cal.firstDayOfWeek = Calendar.MONDAY
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                for (i in 0..6) {
                    val newDate = AppVariables.sdf.format(cal.time)
                    Log.d("DATABASE", "  newDate: $newDate")
                    fetchAndAddData(userId, newDate, cal.get(Calendar.DAY_OF_MONTH), data)
                    cal.add(Calendar.DATE, 1)
                }
            }
            "month" -> {
                startDate = date.substring(0, 8) + "01"
                cal.time = AppVariables.sdf.parse(startDate)!!
                val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
                for (i in 0 until daysInMonth) {
                    cal.set(Calendar.DAY_OF_MONTH, i + 1)
                    val newDate = AppVariables.sdf.format(cal.time)
                    fetchAndAddData(userId, newDate, cal.get(Calendar.MONTH) + 1, data)
                }
            }
            "year" -> {
                runBlocking {
                    fetchHydrationDataForYear(email, startDate.substring(0, 4), data)
                }
            }
            else -> {
                fetchAndAddData(userId, startDate, date.substring(5, 7).toInt(), data)
            }
        }

        Log.d("DATABASE", " DATABASE: $data")

        // Check if data is empty and return an empty list
        return if (data.isEmpty()) emptyList() else data
    }

    /**
     * Fetches hydration data for each month of a specified year for a user.
     *
     * @param email The email of the user.
     * @param year The year for which to fetch the data.
     * @return A list of triples, where each triple represents the total goal, total value of the day, and the month for each month of the year.
     */
    private suspend fun fetchHydrationDataForYear(
        email: String,
        year: String,
        data: MutableList<Triple<Int, Int, Int>>
    ) = withContext(Dispatchers.IO) {
        val userId =  getUserIdByEmail(readableDatabase, email) ?: return@withContext

        for (i in 1..12) {
            val cursor = readableDatabase.rawQuery(
                """
            SELECT SUM(goal) as total_goal, SUM(value_of_day) as total_value
            FROM HydrationForDay
            WHERE user_id=? AND strftime('%Y', date)=? AND strftime('%m', date)=?
            """, arrayOf(userId.toString(), year, i.toString().padStart(2, '0'))
            )

            cursor.use {
                if (it.count == 0) {
                    // If no data is found, add a Triple with 0 values
                    data.add(Triple(0, 0, i))
                } else {
                    while (it.moveToNext()) {
                        val goalIndex = it.getColumnIndex("total_goal")
                        val valueIndex = it.getColumnIndex("total_value")
                        val goal = if (goalIndex != -1) it.getInt(goalIndex) else 0
                        val value = if (valueIndex != -1) it.getInt(valueIndex) else 0
                        data.add(Triple(goal, value, i))
                    }
                }
            }
        }
        Log.d("DATABASE THREAD", " DATABASE: $data")
    }

    /**
     * This function fetches hydration data for a given user and date, and adds it to a provided list.
     * @param userId The ID of the user for whom the data is being fetched.
     * @param date The date for which the data is being fetched.
     * @param timeUnitValue The time unit value (could be day or month) for which the data is being fetched. This is used when adding data to the list.
     * @param data The list to which the fetched data is added. Each element in the list is a Triple containing the hydration goal, the value for the day, and the time unit value.
     */
    private fun fetchAndAddData(
        userId: Int,
        date: String,
        timeUnitValue: Int,
        data: MutableList<Triple<Int, Int, Int>>
    ) {
        val cursor = readableDatabase.query(
            "HydrationForDay",
            arrayOf("goal", "value_of_day"),
            "user_id=? AND date=?",
            arrayOf(userId.toString(), date),
            null,
            null,
            null
        )

        cursor.use {
            if (it.count == 0) {
                // If no data is found, add a Triple with 0 values
                data.add(Triple(0, 0, timeUnitValue))
            } else {
                while (it.moveToNext()) {
                    val goalIndex = it.getColumnIndex("goal")
                    val valueIndex = it.getColumnIndex("value_of_day")
                    val goal = if (goalIndex != -1) it.getInt(goalIndex) else 0
                    val value = if (valueIndex != -1) it.getInt(valueIndex) else 0
                    data.add(Triple(goal, value, timeUnitValue))
                }
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
     * If no matching record is found, returns a pair of null values.
     */
    fun fetchHydrationData(email: String, date: String): Pair<Int?, Int?> {
        val userId =  getUserIdByEmail(readableDatabase, email) ?: return Pair(0, 0)
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
     * Inserts a user profile with default values for the specified user email into the "UserProfile" table.
     *
     * This function retrieves the user ID associated with the given email,
     * and then inserts a user profile with default values (empty strings and 0.0 for height and weight)
     * into the "UserProfile" table for that user ID.
     *
     * @param email The email of the user for whom the profile is being inserted.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun insertUserWithProfile(email: String) {
        val userId = getUserIdByEmail(readableDatabase, email) ?: return

        // Create a default UserInfo object with empty values and 0.0 for height and weight
        val defaultUserInfo = UserInfo("", "", "", 0f, 0f, false)

        val values = ContentValues().apply {
            // Set default user profile values
            put("user_id", userId)
            put("name", defaultUserInfo.name)
            put("gender", defaultUserInfo.gender)
            put("activity_level", defaultUserInfo.activityLevel)
            put("height", defaultUserInfo.height)
            put("weight", defaultUserInfo.weight)
        }

        Log.d("DATABASE", "USER INSERTED")
        writableDatabase.insert("UserProfile", null, values)
    }



    /**
     * Retrieves user profile information from the "UserProfile" table.
     *
     * @param email The email of the user
     * @return An instance of UserProfileInfo containing the retrieved user profile information,
     * or null if no profile is found.
     */
    fun getUserProfile(email: String): UserInfo? {
        val db = readableDatabase
        val userId = getUserIdByEmail(readableDatabase, email) ?: return UserInfo("","","",0f,0f, false)
        val cursor = db.query(
            "UserProfile",
            arrayOf("name", "gender", "activity_level", "height", "weight", "recalculate"),
            "user_id=?",
            arrayOf(userId.toString()),
            null,
            null,
            null
        )

        var name: String? = ""
        var gender: String? = ""
        var activityLevel: String? = ""
        var height: Float? = 0f
        var weight: Float? = 0f
        var recalculate: Boolean

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
                recalculate = getColumnValue("recalculate")?.toInt() == 1
            } else {
                // No user profile found, return null
                return null
            }
        }
        Log.d("RETRIEVED", "USER INSERTED : $recalculate")
        return UserInfo(name, gender, activityLevel, height, weight, recalculate)
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
        weight: Float,
        recalculate: Boolean
    ) {
        val userId = getUserIdByEmail(readableDatabase, email) ?: return
        val values = ContentValues().apply {
            put("name", name)
            put("gender", gender)
            put("activity_level", activityLevel)
            put("height", height)
            put("weight", weight)
            put("user_id", userId)
            put("recalculate", recalculate)
        }

        val affectedRows = writableDatabase.update(
            "UserProfile",
            values,
            "user_id = ?",
            arrayOf(userId.toString())
        )
        Log.d("RETRIEVED", "recalculate: $recalculate")
        Log.d("RETRIEVED", "recalculate: ${AppVariables.hydrationGoalManuallySet.value}")
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

    //DEMO DATA

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
    private suspend fun insertTestUsers(db: SQLiteDatabase?) = withContext(Dispatchers.IO) {
        val salt = generateSalt()
        val hashedPassword = hashPassword("test", salt)

        db?.execSQL("INSERT INTO users (email, hashed_password, salt, created_at) VALUES ('test', '$hashedPassword', '$salt', '2022-01-01')")
    }

    /**
     * This function is used to insert test data into the database.
     * It runs on the IO dispatcher to avoid blocking the main thread.
     *
     * @param db The SQLiteDatabase instance where the data will be inserted.
     *
     * The function performs the following operations:
     * 1. Inserts a test user profile into the UserProfile table.
     * 2. Inserts hydration data for different dates within the current year and the previous year into the HydrationForDay table.
     *
     * The hydration data is generated as follows:
     * - For each day of each month of the current year and the previous year, a random hydration value between 1000 and 4000 is generated.
     * - A random day in each month is left empty.
     * - This hydration value, along with the date and a fixed goal of 3000, is inserted into the HydrationForDay table for the test user.
     *
     * Note: This function is marked with the @RequiresApi annotation to indicate that it requires the API level specified (in this case, Build.VERSION_CODES.O) or higher.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun insertTestData(db: SQLiteDatabase?) = withContext(Dispatchers.IO) {
        // Insert test user profiles
        Log.d("DATABASE", "Getting DemoDATA")
        db?.execSQL("INSERT INTO UserProfile (user_id, name, gender, activity_level, height, weight) VALUES (1, 'John Doe', 'Male', 'Active', 175, 70)")
        Log.d("DATABASE", "Getting DemoDATA")
        // Insert hydration data for different dates within this year and the previous year
        val random = Random()
        val currentYear = Year.now().value
        val previousYear = currentYear - 1
        val today = LocalDate.now()

        // Pick a random month to leave empty
        val emptyMonth = random.nextInt(12) + 1

        for (year in listOf(previousYear, currentYear)) {
            for (month in 1..12) {
                // Skip the randomly chosen month
                if (month == emptyMonth) {
                    continue
                }
                val date = LocalDate.of(year, month, 1)
                // Use date.lengthOfMonth() to ensure we cover all days in the month
                for (day in 1..date.lengthOfMonth()) {
                    // Pick a random day to leave empty
                    val emptyDay = random.nextInt(date.lengthOfMonth()) + 1
                    if (day == emptyDay) {
                        continue
                    }
                    val dateWithDay = LocalDate.of(year, month, day)
                    if (dateWithDay.isAfter(today)) {
                        // Skip this date if it's in the future
                        continue
                    }
                    val dateString = dateWithDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    val valueOfDay = random.nextInt(3000) + 1000  // Random hydration value between 1000 and 4000
                    val goal = 3000

                    db?.execSQL("INSERT INTO HydrationForDay (date, user_id, value_of_day, goal) VALUES ('$dateString', 1, $valueOfDay, $goal)")
                }
            }
        }
    }
}
